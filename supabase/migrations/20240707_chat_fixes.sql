-- 1. Add INSERT policy for room_messages
-- Allow authenticated users to insert messages if they are the author
DROP POLICY IF EXISTS "Allow authenticated users to insert messages" ON public.room_messages;
CREATE POLICY "Allow authenticated users to insert messages"
    ON public.room_messages FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = user_id);

-- 2. Add room_messages to realtime publication
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_publication_tables
        WHERE pubname = 'supabase_realtime'
        AND schemaname = 'public'
        AND tablename = 'room_messages'
    ) THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE public.room_messages;
    END IF;
END $$;
