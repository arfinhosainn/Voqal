-- 1. Safely handle rooms.id type conversion (Fixes legacy non-UUID strings)
DO $$
BEGIN
    -- Check if rooms exists and if ID is text
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'rooms' AND column_name = 'id' AND data_type = 'text'
    ) THEN
        -- Drop view that might depend on it
        DROP VIEW IF EXISTS public.room_discovery_view;

        -- Step A: Add a temporary UUID column
        ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS id_new UUID DEFAULT gen_random_uuid();

        -- Step B: Drop the old ID column (and its primary key constraint)
        -- We have to drop constraints first
        ALTER TABLE public.rooms DROP CONSTRAINT IF EXISTS rooms_pkey CASCADE;
        ALTER TABLE public.rooms DROP COLUMN id;

        -- Step C: Rename new column to id and make it primary key
        ALTER TABLE public.rooms RENAME COLUMN id_new TO id;
        ALTER TABLE public.rooms ADD PRIMARY KEY (id);
    END IF;
END $$;

-- 2. Create Core Tables (if they don't exist)
CREATE TABLE IF NOT EXISTS public.rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    host_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    category TEXT NOT NULL,
    visibility TEXT DEFAULT 'public',
    status TEXT DEFAULT 'live',
    listener_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    participant_preview JSONB DEFAULT '[]',
    last_activity_at TIMESTAMPTZ DEFAULT now(),
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS public.room_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID REFERENCES public.rooms(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    joined_at TIMESTAMPTZ DEFAULT now(),
    left_at TIMESTAMPTZ,
    is_speaker BOOLEAN DEFAULT false,
    hand_raised BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS public.room_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID REFERENCES public.rooms(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- 3. Add Missing Columns (for existing tables)
ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS host_id UUID REFERENCES auth.users(id) ON DELETE CASCADE;
ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS visibility TEXT DEFAULT 'public';
ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS status TEXT DEFAULT 'live';
ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS listener_count INT DEFAULT 0;
ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS comment_count INT DEFAULT 0;
ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS participant_preview JSONB DEFAULT '[]';
ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS last_activity_at TIMESTAMPTZ DEFAULT now();

-- 4. Set Defaults & Constraints
ALTER TABLE public.rooms ALTER COLUMN host_id SET DEFAULT auth.uid();

CREATE UNIQUE INDEX IF NOT EXISTS idx_room_sessions_active_user_room
    ON public.room_sessions (user_id, room_id)
    WHERE (left_at IS NULL);

-- 5. Enable RLS
ALTER TABLE public.rooms ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.room_sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.room_messages ENABLE ROW LEVEL SECURITY;

-- 6. RLS Policies
DROP POLICY IF EXISTS "Allow public read access to live rooms" ON public.rooms;
CREATE POLICY "Allow public read access to live rooms"
    ON public.rooms FOR SELECT
    USING (visibility = 'public');

DROP POLICY IF EXISTS "Allow authenticated users to create rooms" ON public.rooms;
CREATE POLICY "Allow authenticated users to create rooms"
    ON public.rooms FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = host_id);

DROP POLICY IF EXISTS "Allow public read access to active sessions" ON public.room_sessions;
CREATE POLICY "Allow public read access to active sessions"
    ON public.room_sessions FOR SELECT
    USING (true);

DROP POLICY IF EXISTS "Allow public read access to messages" ON public.room_messages;
CREATE POLICY "Allow public read access to messages"
    ON public.room_messages FOR SELECT
    USING (true);

-- 7. Cleanup Legacy Views
DROP VIEW IF EXISTS public.room_discovery_view;

-- 8. Sync Logic (Trigger Function)
CREATE OR REPLACE FUNCTION public.sync_room_metadata()
RETURNS TRIGGER AS $$
DECLARE
    v_room_id UUID;
BEGIN
    IF TG_TABLE_NAME = 'room_sessions' THEN
        v_room_id := COALESCE(NEW.room_id, OLD.room_id);
    ELSIF TG_TABLE_NAME = 'room_messages' THEN
        v_room_id := COALESCE(NEW.room_id, OLD.room_id);
    END IF;

    IF v_room_id IS NOT NULL THEN
        UPDATE public.rooms
        SET
            listener_count = (
                SELECT count(*)
                FROM public.room_sessions
                WHERE room_id = v_room_id AND left_at IS NULL
            ),
            comment_count = (
                SELECT count(*)
                FROM public.room_messages
                WHERE room_id = v_room_id
            ),
            participant_preview = (
                SELECT COALESCE(jsonb_agg(p), '[]'::jsonb)
                FROM (
                    SELECT
                        pr.id,
                        COALESCE(pr.username, pr.first_name, 'User') as name,
                        pr.avatar_path,
                        'US' as country_code -- Placeholder
                    FROM public.room_sessions rs
                    JOIN public.profiles pr ON rs.user_id = pr.id
                    WHERE rs.room_id = v_room_id AND rs.left_at IS NULL
                    ORDER BY rs.joined_at DESC
                    LIMIT 4
                ) p
            ),
            last_activity_at = now()
        WHERE id = v_room_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 9. Automatically Join Host Trigger
CREATE OR REPLACE FUNCTION public.auto_join_host()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.room_sessions (room_id, user_id, is_speaker)
    VALUES (NEW.id, NEW.host_id, true);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS tr_auto_join_host ON public.rooms;
CREATE TRIGGER tr_auto_join_host
AFTER INSERT ON public.rooms
FOR EACH ROW EXECUTE FUNCTION public.auto_join_host();

-- 10. Triggers
DROP TRIGGER IF EXISTS tr_sync_room_metadata_sessions ON public.room_sessions;
CREATE TRIGGER tr_sync_room_metadata_sessions
AFTER INSERT OR UPDATE OR DELETE ON public.room_sessions
FOR EACH ROW EXECUTE FUNCTION public.sync_room_metadata();

DROP TRIGGER IF EXISTS tr_sync_room_metadata_messages ON public.room_messages;
CREATE TRIGGER tr_sync_room_metadata_messages
AFTER INSERT OR UPDATE OR DELETE ON public.room_messages
FOR EACH ROW EXECUTE FUNCTION public.sync_room_metadata();

-- 11. RPC Functions
CREATE OR REPLACE FUNCTION public.join_room(p_room_id UUID)
RETURNS void AS $$
BEGIN
    -- Close any existing active sessions for this user in ANY room
    UPDATE public.room_sessions
    SET left_at = now()
    WHERE user_id = auth.uid() AND left_at IS NULL;

    -- Insert new active session
    INSERT INTO public.room_sessions (room_id, user_id)
    VALUES (p_room_id, auth.uid());
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION public.leave_room(p_room_id UUID)
RETURNS void AS $$
BEGIN
    UPDATE public.room_sessions
    SET left_at = now()
    WHERE user_id = auth.uid() AND room_id = p_room_id AND left_at IS NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 12. Enable Realtime for rooms and room_sessions (Safer Check)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_publication_tables
        WHERE pubname = 'supabase_realtime'
        AND schemaname = 'public'
        AND tablename = 'rooms'
    ) THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE public.rooms;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_publication_tables
        WHERE pubname = 'supabase_realtime'
        AND schemaname = 'public'
        AND tablename = 'room_sessions'
    ) THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE public.room_sessions;
    END IF;
END $$;

-- 13. Backfill
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN SELECT id FROM public.rooms LOOP
        UPDATE public.rooms SET id = id WHERE id = r.id;
    END LOOP;
END;
$$;
