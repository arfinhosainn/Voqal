-- Phase 5: Production Hardening
-- 1. Create user_devices table (push notifications)
-- 2. Add soft delete to rooms
-- 3. Create active_rooms view
-- 4. Create app_config table
-- 5. Update RLS policies for soft delete
-- 6. Update RPC functions

-- ============================================================
-- 0. Ensure trigger function exists
-- ============================================================
CREATE OR REPLACE FUNCTION public.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ============================================================
-- 1. Create user_devices table
-- ============================================================
CREATE TABLE IF NOT EXISTS public.user_devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    device_id TEXT NOT NULL,                    -- Stable installation identifier
    platform TEXT NOT NULL CHECK (platform IN ('ios', 'android', 'web')),
    push_token TEXT NOT NULL,
    device_name TEXT,
    app_version TEXT,                           -- e.g., "1.2.3"
    os_version TEXT,                            -- e.g., "Android 17", "iOS 18.1"
    is_active BOOLEAN DEFAULT true,
    last_seen_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    created_at TIMESTAMPTZ DEFAULT now(),
    
    -- One device row per installation per user
    CONSTRAINT unique_user_device UNIQUE(user_id, device_id),
    
    -- Composite unique for push tokens (different infrastructures per platform)
    CONSTRAINT unique_platform_token UNIQUE(platform, push_token)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_user_devices_user ON public.user_devices(user_id);
CREATE INDEX IF NOT EXISTS idx_user_devices_active ON public.user_devices(user_id, is_active) WHERE is_active = true;

-- Enable RLS
ALTER TABLE public.user_devices ENABLE ROW LEVEL SECURITY;

-- RLS Policies
DROP POLICY IF EXISTS "Users can manage own devices" ON public.user_devices;
CREATE POLICY "Users can manage own devices"
    ON public.user_devices FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Trigger for updated_at
DROP TRIGGER IF EXISTS tr_user_devices_updated_at ON public.user_devices;
CREATE TRIGGER tr_user_devices_updated_at
    BEFORE UPDATE ON public.user_devices
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============================================================
-- 2. Add soft delete to rooms
-- ============================================================
ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

-- Index for soft delete queries
CREATE INDEX IF NOT EXISTS idx_rooms_deleted ON public.rooms(deleted_at) WHERE deleted_at IS NOT NULL;

-- ============================================================
-- 3. Create active_rooms view
-- ============================================================
CREATE OR REPLACE VIEW public.active_rooms AS
SELECT * FROM public.rooms
WHERE deleted_at IS NULL;

-- ============================================================
-- 4. Create app_config table
-- ============================================================
CREATE TABLE IF NOT EXISTS public.app_config (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS
ALTER TABLE public.app_config ENABLE ROW LEVEL SECURITY;

-- RLS Policies - Public read, service role write
DROP POLICY IF EXISTS "Public can read app_config" ON public.app_config;
CREATE POLICY "Public can read app_config"
    ON public.app_config FOR SELECT
    USING (true);

-- Note: Write access restricted to service_role (backend only)
-- No INSERT/UPDATE policy for authenticated users

-- Trigger for updated_at
DROP TRIGGER IF EXISTS tr_app_config_updated_at ON public.app_config;
CREATE TRIGGER tr_app_config_updated_at
    BEFORE UPDATE ON public.app_config
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- Seed default config values
INSERT INTO public.app_config (key, value) VALUES
    ('maintenance_mode', 'false'),
    ('minimum_supported_version', '1.0.0'),
    ('force_update_version', ''),
    ('max_room_size', '1000'),
    ('featured_room_limit', '10')
ON CONFLICT (key) DO NOTHING;

-- ============================================================
-- 5. Update RLS policies for rooms soft delete
-- ============================================================

-- Drop and recreate the public read policy to exclude deleted rooms
DROP POLICY IF EXISTS "Allow public read access to live rooms" ON public.rooms;
CREATE POLICY "Allow public read access to live rooms"
    ON public.rooms FOR SELECT
    USING (visibility = 'public' AND status = 'live' AND deleted_at IS NULL);

-- Add policy for hosts to view their own deleted rooms
DROP POLICY IF EXISTS "Hosts can view own deleted rooms" ON public.rooms;
CREATE POLICY "Hosts can view own deleted rooms"
    ON public.rooms FOR SELECT
    TO authenticated
    USING (auth.uid() = host_id);

-- ============================================================
-- 6. Update RPC functions for soft delete
-- ============================================================

-- Update join_room to check room is not deleted
CREATE OR REPLACE FUNCTION public.join_room(p_room_id UUID)
RETURNS void AS $$
BEGIN
    -- Close any existing active sessions for this user in ANY room
    UPDATE public.room_sessions
    SET left_at = now()
    WHERE user_id = auth.uid() AND left_at IS NULL;

    -- Insert new active session (only if room exists and not deleted)
    INSERT INTO public.room_sessions (room_id, user_id, last_heartbeat_at)
    SELECT p_room_id, auth.uid(), now()
    FROM public.rooms
    WHERE id = p_room_id AND deleted_at IS NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Update reconcile_stale_rooms to skip deleted rooms
CREATE OR REPLACE FUNCTION public.reconcile_stale_rooms()
RETURNS void AS $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT rooms.id
        FROM public.rooms
        WHERE rooms.status = 'live'
          AND rooms.deleted_at IS NULL
          AND rooms.last_activity_at < now() - interval '5 minutes'
          AND NOT EXISTS (
              SELECT 1
              FROM public.room_sessions
              WHERE room_sessions.room_id = rooms.id
                AND room_sessions.left_at IS NULL
          )
    LOOP
        UPDATE public.rooms
        SET status = 'ended',
            last_activity_at = now()
        WHERE id = r.id;

        DELETE FROM public.room_sessions
        WHERE room_id = r.id AND left_at IS NULL;
    END LOOP;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ============================================================
-- 7. Helper function to soft delete a room
-- ============================================================
CREATE OR REPLACE FUNCTION public.delete_room(p_room_id UUID)
RETURNS void AS $$
BEGIN
    UPDATE public.rooms
    SET deleted_at = now(),
        status = 'ended'
    WHERE id = p_room_id
      AND deleted_at IS NULL
      AND auth.uid() = host_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ============================================================
-- 8. Helper function to restore a deleted room
-- ============================================================
CREATE OR REPLACE FUNCTION public.restore_room(p_room_id UUID)
RETURNS void AS $$
BEGIN
    UPDATE public.rooms
    SET deleted_at = NULL,
        status = 'live'
    WHERE id = p_room_id
      AND deleted_at IS NOT NULL
      AND auth.uid() = host_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ============================================================
-- 9. Helper function to manage device push tokens
-- ============================================================
CREATE OR REPLACE FUNCTION public.register_device(
    p_device_id TEXT,
    p_platform TEXT,
    p_push_token TEXT,
    p_device_name TEXT DEFAULT NULL,
    p_app_version TEXT DEFAULT NULL,
    p_os_version TEXT DEFAULT NULL
)
RETURNS void AS $$
BEGIN
    INSERT INTO public.user_devices (
        user_id, 
        device_id, 
        platform, 
        push_token, 
        device_name,
        app_version,
        os_version,
        is_active,
        last_seen_at
    )
    VALUES (
        auth.uid(),
        p_device_id,
        p_platform,
        p_push_token,
        p_device_name,
        p_app_version,
        p_os_version,
        true,
        now()
    )
    ON CONFLICT (user_id, device_id) DO UPDATE SET
        push_token = EXCLUDED.push_token,
        device_name = COALESCE(EXCLUDED.device_name, user_devices.device_name),
        app_version = COALESCE(EXCLUDED.app_version, user_devices.app_version),
        os_version = COALESCE(EXCLUDED.os_version, user_devices.os_version),
        is_active = true,
        last_seen_at = now(),
        updated_at = now();
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ============================================================
-- 10. Helper function to deactivate old devices
-- ============================================================
CREATE OR REPLACE FUNCTION public.deactivate_device(p_device_id TEXT)
RETURNS void AS $$
BEGIN
    UPDATE public.user_devices
    SET is_active = false,
        updated_at = now()
    WHERE user_id = auth.uid()
      AND device_id = p_device_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
