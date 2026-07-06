-- Add new columns to public.rooms
ALTER TABLE public.rooms
ADD COLUMN IF NOT EXISTS status TEXT DEFAULT 'live',
ADD COLUMN IF NOT EXISTS listener_count INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS comment_count INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS participant_preview JSONB DEFAULT '[]',
ADD COLUMN IF NOT EXISTS last_activity_at TIMESTAMPTZ DEFAULT now();

-- Update trigger function to sync room metadata
CREATE OR REPLACE FUNCTION public.sync_room_metadata()
RETURNS TRIGGER AS $$
DECLARE
    v_room_id UUID;
BEGIN
    -- Determine the room_id based on the table the trigger fired on
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
                        pr.username as name,
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

-- Create triggers
DROP TRIGGER IF EXISTS tr_sync_room_metadata_sessions ON public.room_sessions;
CREATE TRIGGER tr_sync_room_metadata_sessions
AFTER INSERT OR UPDATE OR DELETE ON public.room_sessions
FOR EACH ROW EXECUTE FUNCTION public.sync_room_metadata();

DROP TRIGGER IF EXISTS tr_sync_room_metadata_messages ON public.room_messages;
CREATE TRIGGER tr_sync_room_metadata_messages
AFTER INSERT OR UPDATE OR DELETE ON public.room_messages
FOR EACH ROW EXECUTE FUNCTION public.sync_room_metadata();

-- Backfill existing data
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN SELECT id FROM public.rooms LOOP
        UPDATE public.rooms SET id = id WHERE id = r.id; -- Triggers the update logic
    END LOOP;
END;
$$;
