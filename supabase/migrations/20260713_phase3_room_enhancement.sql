-- Phase 3: Room System Enhancement
-- 1. Enhance rooms table (status, visibility, type, scheduled_for)
-- 2. Enhance room_sessions with role enum
-- 3. Create user_room_history table
-- 4. Create room_invites table
-- 5. Create room_reminders table

-- 1. Enhance rooms table
ALTER TABLE public.rooms
ADD COLUMN IF NOT EXISTS status TEXT DEFAULT 'live' CHECK (status IN ('scheduled', 'live', 'ended', 'cancelled')),
ADD COLUMN IF NOT EXISTS visibility TEXT DEFAULT 'public' CHECK (visibility IN ('public', 'social', 'private')),
ADD COLUMN IF NOT EXISTS type TEXT DEFAULT 'voice' CHECK (type IN ('voice', 'podcast', 'event')),
ADD COLUMN IF NOT EXISTS scheduled_for TIMESTAMPTZ,
ADD COLUMN IF NOT EXISTS ended_at TIMESTAMPTZ;

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_rooms_status ON public.rooms(status);
CREATE INDEX IF NOT EXISTS idx_rooms_visibility ON public.rooms(visibility);
CREATE INDEX IF NOT EXISTS idx_rooms_scheduled_for ON public.rooms(scheduled_for) WHERE scheduled_for IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_rooms_host_status ON public.rooms(host_id, status);

-- 2. Enhance room_sessions with role
ALTER TABLE public.room_sessions
ADD COLUMN IF NOT EXISTS role TEXT DEFAULT 'listener' CHECK (role IN ('host', 'moderator', 'speaker', 'listener'));

-- Backfill: set host role for existing host sessions
UPDATE public.room_sessions rs
SET role = 'host'
FROM public.rooms r
WHERE rs.room_id = r.id AND rs.user_id = r.host_id AND rs.role = 'listener';

-- 3. Create user_room_history table
CREATE TABLE IF NOT EXISTS public.user_room_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    room_id UUID NOT NULL REFERENCES public.rooms(id) ON DELETE CASCADE,
    role TEXT NOT NULL CHECK (role IN ('host', 'moderator', 'speaker', 'listener')),
    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    left_at TIMESTAMPTZ,
    duration_seconds INT GENERATED ALWAYS AS (
        CASE 
            WHEN left_at IS NOT NULL THEN EXTRACT(EPOCH FROM (left_at - joined_at))::INT
            ELSE NULL
        END
    ) STORED
);

CREATE INDEX IF NOT EXISTS idx_user_room_history_user ON public.user_room_history(user_id, joined_at DESC);
CREATE INDEX IF NOT EXISTS idx_user_room_history_room ON public.user_room_history(room_id, joined_at);

-- 4. Create room_invites table
CREATE TABLE IF NOT EXISTS public.room_invites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL REFERENCES public.rooms(id) ON DELETE CASCADE,
    from_user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    to_user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'declined', 'expired')),
    created_at TIMESTAMPTZ DEFAULT now(),
    responded_at TIMESTAMPTZ,
    UNIQUE (room_id, to_user_id)
);

CREATE INDEX IF NOT EXISTS idx_room_invites_to_user ON public.room_invites(to_user_id, status);
CREATE INDEX IF NOT EXISTS idx_room_invites_from_user ON public.room_invites(from_user_id);

-- 5. Create room_reminders table
CREATE TABLE IF NOT EXISTS public.room_reminders (
    room_id UUID NOT NULL REFERENCES public.rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (room_id, user_id)
);

-- 6. Enable RLS
ALTER TABLE public.user_room_history ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.room_invites ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.room_reminders ENABLE ROW LEVEL SECURITY;

-- 7. RLS Policies for user_room_history
DROP POLICY IF EXISTS "Users can view own room history" ON public.user_room_history;
CREATE POLICY "Users can view own room history"
    ON public.user_room_history FOR SELECT
    TO authenticated
    USING (auth.uid() = user_id);

-- Public can view room history (for stats, etc.)
DROP POLICY IF EXISTS "Public can view room history" ON public.user_room_history;
CREATE POLICY "Public can view room history"
    ON public.user_room_history FOR SELECT
    USING (true);

-- System can insert history
DROP POLICY IF EXISTS "System can insert room history" ON public.user_room_history;
CREATE POLICY "System can insert room history"
    ON public.user_room_history FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = user_id);

-- 8. RLS Policies for room_invites
DROP POLICY IF EXISTS "Users can view own invites" ON public.room_invites;
CREATE POLICY "Users can view own invites"
    ON public.room_invites FOR SELECT
    TO authenticated
    USING (auth.uid() = from_user_id OR auth.uid() = to_user_id);

DROP POLICY IF EXISTS "Users can create invites" ON public.room_invites;
CREATE POLICY "Users can create invites"
    ON public.room_invites FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = from_user_id);

DROP POLICY IF EXISTS "Users can respond to own invites" ON public.room_invites;
CREATE POLICY "Users can respond to own invites"
    ON public.room_invites FOR UPDATE
    TO authenticated
    USING (auth.uid() = to_user_id)
    WITH CHECK (auth.uid() = to_user_id);

-- 9. RLS Policies for room_reminders
DROP POLICY IF EXISTS "Users can manage own reminders" ON public.room_reminders;
CREATE POLICY "Users can manage own reminders"
    ON public.room_reminders FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- 10. Triggers for updating user_room_history on room_sessions changes
CREATE OR REPLACE FUNCTION public.sync_user_room_history()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- User joined room
        INSERT INTO public.user_room_history (user_id, room_id, role, joined_at)
        VALUES (NEW.user_id, NEW.room_id, NEW.role, NEW.joined_at)
        ON CONFLICT DO NOTHING;
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' AND NEW.left_at IS NOT NULL AND OLD.left_at IS NULL THEN
        -- User left room
        UPDATE public.user_room_history
        SET left_at = NEW.left_at
        WHERE user_id = NEW.user_id AND room_id = NEW.room_id AND left_at IS NULL;
        RETURN NEW;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS tr_sync_user_room_history ON public.room_sessions;
CREATE TRIGGER tr_sync_user_room_history
    AFTER INSERT OR UPDATE ON public.room_sessions
    FOR EACH ROW EXECUTE FUNCTION public.sync_user_room_history();

-- 11. Trigger to auto-join host when room created (already exists, but ensure role='host')
-- The auto_join_host function in earlier migration already handles this

-- 12. Trigger for updating profile_stats on room join/create
CREATE OR REPLACE FUNCTION public.update_room_stats()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- Increment rooms_joined for participant
        UPDATE public.profile_stats
        SET rooms_joined = rooms_joined + 1,
            last_updated = now()
        WHERE user_id = NEW.user_id;
        
        -- If host, increment rooms_hosted
        IF NEW.role = 'host' THEN
            UPDATE public.profile_stats
            SET rooms_hosted = rooms_hosted + 1,
                last_updated = now()
            WHERE user_id = NEW.user_id;
        END IF;
        
        RETURN NEW;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS tr_update_room_stats ON public.room_sessions;
CREATE TRIGGER tr_update_room_stats
    AFTER INSERT ON public.room_sessions
    FOR EACH ROW EXECUTE FUNCTION public.update_room_stats();

-- 13. Trigger for rooms_created stat on room creation
CREATE OR REPLACE FUNCTION public.update_rooms_created_stat()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE public.profile_stats
        SET rooms_created = rooms_created + 1,
            last_updated = now()
        WHERE user_id = NEW.host_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS tr_rooms_created_stat ON public.rooms;
CREATE TRIGGER tr_rooms_created_stat
    AFTER INSERT ON public.rooms
    FOR EACH ROW EXECUTE FUNCTION public.update_rooms_created_stat();