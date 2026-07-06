-- Create Rooms Table
CREATE TABLE IF NOT EXISTS public.rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    host_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    category TEXT NOT NULL,
    visibility TEXT DEFAULT 'public',
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS on Rooms
ALTER TABLE public.rooms ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow public read access to live rooms"
    ON public.rooms FOR SELECT
    USING (visibility = 'public');

CREATE POLICY "Allow authenticated users to create rooms"
    ON public.rooms FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = host_id);

-- Create Room Sessions Table
CREATE TABLE IF NOT EXISTS public.room_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID REFERENCES public.rooms(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    joined_at TIMESTAMPTZ DEFAULT now(),
    left_at TIMESTAMPTZ,
    is_speaker BOOLEAN DEFAULT false,
    hand_raised BOOLEAN DEFAULT false
);

-- Constraint: Only one active session per user per room
CREATE UNIQUE INDEX IF NOT EXISTS idx_room_sessions_active_user_room
    ON public.room_sessions (user_id, room_id)
    WHERE (left_at IS NULL);

-- Enable RLS on Room Sessions
ALTER TABLE public.room_sessions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow public read access to active sessions"
    ON public.room_sessions FOR SELECT
    USING (true);

-- Create Room Messages Table
CREATE TABLE IF NOT EXISTS public.room_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID REFERENCES public.rooms(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS on Room Messages
ALTER TABLE public.room_messages ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow public read access to messages"
    ON public.room_messages FOR SELECT
    USING (true);

-- Create Discovery View
CREATE OR REPLACE VIEW public.room_discovery_view AS
SELECT
    r.id,
    r.title,
    r.category,
    r.created_at,
    (
        SELECT count(*)
        FROM public.room_sessions rs
        WHERE rs.room_id = r.id AND rs.left_at IS NULL
    ) as listener_count,
    (
        SELECT count(*)
        FROM public.room_messages rm
        WHERE rm.room_id = r.id
    ) as comment_count,
    (
        SELECT jsonb_agg(p)
        FROM (
            SELECT
                pr.id,
                pr.username as name,
                pr.avatar_path,
                'US' as country_code -- Placeholder, replace with actual country field if available
            FROM public.room_sessions rs
            JOIN public.profiles pr ON rs.user_id = pr.id
            WHERE rs.room_id = r.id AND rs.left_at IS NULL
            ORDER BY rs.joined_at DESC
            LIMIT 4
        ) p
    ) as participant_preview,
    GREATEST(
        r.created_at,
        (SELECT MAX(joined_at) FROM public.room_sessions rs WHERE rs.room_id = r.id),
        (SELECT MAX(created_at) FROM public.room_messages rm WHERE rm.room_id = r.id)
    ) as last_activity_at
FROM public.rooms r
WHERE r.visibility = 'public';

-- RPC: join_room
CREATE OR REPLACE FUNCTION public.join_room(p_room_id UUID)
RETURNS void AS $$
BEGIN
    -- Close any existing stale sessions for this user in this room
    UPDATE public.room_sessions
    SET left_at = now()
    WHERE user_id = auth.uid() AND room_id = p_room_id AND left_at IS NULL;

    -- Insert new active session
    INSERT INTO public.room_sessions (room_id, user_id)
    VALUES (p_room_id, auth.uid());
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- RPC: leave_room
CREATE OR REPLACE FUNCTION public.leave_room(p_room_id UUID)
RETURNS void AS $$
BEGIN
    UPDATE public.room_sessions
    SET left_at = now()
    WHERE user_id = auth.uid() AND room_id = p_room_id AND left_at IS NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
