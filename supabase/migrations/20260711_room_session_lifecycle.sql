-- 1. Reconciliation: clean up rooms with no active sessions (safety net)
CREATE OR REPLACE FUNCTION public.reconcile_stale_rooms()
RETURNS void AS $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT rooms.id
        FROM public.rooms
        WHERE rooms.status = 'live'
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

        DELETE FROM public.rooms
        WHERE id = r.id;
    END LOOP;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 2. Schedule reconciliation every 5 minutes (requires pg_cron)
-- Uncomment if pg_cron is available:
-- SELECT cron.schedule('reconcile-stale-rooms', '*/5 * * * *', 'SELECT public.reconcile_stale_rooms()');

-- 3. Update discovery query to exclude ended rooms
DROP POLICY IF EXISTS "Allow public read access to live rooms" ON public.rooms;
CREATE POLICY "Allow public read access to live rooms"
    ON public.rooms FOR SELECT
    USING (visibility = 'public' AND status = 'live');
