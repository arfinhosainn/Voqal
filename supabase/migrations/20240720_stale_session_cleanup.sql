-- 1. Add last_heartbeat_at to track client activity
ALTER TABLE public.room_sessions ADD COLUMN IF NOT EXISTS last_heartbeat_at TIMESTAMPTZ DEFAULT now();

-- 2. Update join_room to set initial heartbeat
CREATE OR REPLACE FUNCTION public.join_room(p_room_id UUID)
RETURNS void AS $$
BEGIN
    UPDATE public.room_sessions
    SET left_at = now()
    WHERE user_id = auth.uid() AND left_at IS NULL;

    INSERT INTO public.room_sessions (room_id, user_id, last_heartbeat_at)
    VALUES (p_room_id, auth.uid(), now());
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 3. RPC for client to update heartbeat
CREATE OR REPLACE FUNCTION public.update_heartbeat()
RETURNS void AS $$
BEGIN
    UPDATE public.room_sessions
    SET last_heartbeat_at = now()
    WHERE user_id = auth.uid() AND left_at IS NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 4. RPC to clean up stale sessions (no heartbeat for >90 seconds)
CREATE OR REPLACE FUNCTION public.cleanup_stale_sessions()
RETURNS void AS $$
BEGIN
    UPDATE public.room_sessions
    SET left_at = now()
    WHERE left_at IS NULL
    AND last_heartbeat_at < now() - interval '90 seconds';
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 5. Schedule cleanup via pg_cron (available on Supabase Pro)
-- Commented out by default; uncomment if pg_cron is enabled
-- SELECT cron.schedule(
--     'cleanup-stale-sessions',
--     '* * * * *',
--     'SELECT public.cleanup_stale_sessions()'
-- );
