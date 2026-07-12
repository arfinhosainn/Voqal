-- Phase 4: Safety & Engagement
-- 1. user_blocks
-- 2. user_mutes
-- 3. user_reports
-- 4. notifications
-- 5. user_presence

-- 1. user_blocks
CREATE TABLE IF NOT EXISTS public.user_blocks (
    blocker_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (blocker_id, blocked_id)
);

-- Prevent self-block
ALTER TABLE public.user_blocks
ADD CONSTRAINT no_self_block CHECK (blocker_id != blocked_id);

CREATE INDEX IF NOT EXISTS idx_user_blocks_blocked ON public.user_blocks(blocked_id);

-- 2. user_mutes
CREATE TABLE IF NOT EXISTS public.user_mutes (
    muter_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    muted_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (muter_id, muted_id)
);

ALTER TABLE public.user_mutes
ADD CONSTRAINT no_self_mute CHECK (muter_id != muted_id);

CREATE INDEX IF NOT EXISTS idx_user_mutes_muted ON public.user_mutes(muted_id);

-- 3. user_reports
CREATE TABLE IF NOT EXISTS public.user_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    reported_user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    reason TEXT NOT NULL CHECK (reason IN ('spam', 'harassment', 'hate_speech', 'violence', 'sexual_content', 'fake_account', 'other')),
    description TEXT,
    status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'reviewed', 'action_taken', 'dismissed')),
    created_at TIMESTAMPTZ DEFAULT now(),
    reviewed_at TIMESTAMPTZ,
    reviewed_by UUID REFERENCES public.profiles(id)
);

CREATE INDEX IF NOT EXISTS idx_user_reports_reporter ON public.user_reports(reporter_id);
CREATE INDEX IF NOT EXISTS idx_user_reports_reported ON public.user_reports(reported_user_id);
CREATE INDEX IF NOT EXISTS idx_user_reports_status ON public.user_reports(status);

-- 4. notifications
CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    actor_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    type TEXT NOT NULL CHECK (type IN (
        'follow', 'room_invite', 'room_starting', 'room_live', 
        'mention', 'tip_received', 'badge_earned', 
        'report_action', 'system'
    )),
    entity_id UUID, -- room_id, user_id, etc.
    entity_type TEXT CHECK (entity_type IN ('room', 'user', 'message', 'report')),
    title TEXT NOT NULL,
    body TEXT,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_notifications_user ON public.notifications(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_unread ON public.notifications(user_id, read_at) WHERE read_at IS NULL;

-- 5. user_presence
CREATE TABLE IF NOT EXISTS public.user_presence (
    user_id UUID PRIMARY KEY REFERENCES public.profiles(id) ON DELETE CASCADE,
    status TEXT NOT NULL DEFAULT 'offline' CHECK (status IN ('online', 'away', 'in_room', 'offline')),
    current_room_id UUID REFERENCES public.rooms(id) ON DELETE SET NULL,
    last_seen_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- 6. Enable RLS
ALTER TABLE public.user_blocks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_mutes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_reports ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_presence ENABLE ROW LEVEL SECURITY;

-- 7. RLS Policies

-- user_blocks: users manage their own blocks, public can check if blocked
DROP POLICY IF EXISTS "Users can manage own blocks" ON public.user_blocks;
CREATE POLICY "Users can manage own blocks"
    ON public.user_blocks FOR ALL
    TO authenticated
    USING (auth.uid() = blocker_id)
    WITH CHECK (auth.uid() = blocker_id);

DROP POLICY IF EXISTS "Public can check blocks" ON public.user_blocks;
CREATE POLICY "Public can check blocks"
    ON public.user_blocks FOR SELECT
    USING (true);

-- user_mutes: similar to blocks
DROP POLICY IF EXISTS "Users can manage own mutes" ON public.user_mutes;
CREATE POLICY "Users can manage own mutes"
    ON public.user_mutes FOR ALL
    TO authenticated
    USING (auth.uid() = muter_id)
    WITH CHECK (auth.uid() = muter_id);

DROP POLICY IF EXISTS "Public can check mutes" ON public.user_mutes;
CREATE POLICY "Public can check mutes"
    ON public.user_mutes FOR SELECT
    USING (true);

-- user_reports: reporter can create/view own, reported user can view, mods can manage
DROP POLICY IF EXISTS "Users can create reports" ON public.user_reports;
CREATE POLICY "Users can create reports"
    ON public.user_reports FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = reporter_id);

DROP POLICY IF EXISTS "Reporter can view own reports" ON public.user_reports;
CREATE POLICY "Reporter can view own reports"
    ON public.user_reports FOR SELECT
    TO authenticated
    USING (auth.uid() = reporter_id);

DROP POLICY IF EXISTS "Reported user can view reports against them" ON public.user_reports;
CREATE POLICY "Reported user can view reports against them"
    ON public.user_reports FOR SELECT
    TO authenticated
    USING (auth.uid() = reported_user_id);

-- notifications: users manage their own
DROP POLICY IF EXISTS "Users can manage own notifications" ON public.notifications;
CREATE POLICY "Users can manage own notifications"
    ON public.notifications FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- user_presence: users manage own, public can read
DROP POLICY IF EXISTS "Users can manage own presence" ON public.user_presence;
CREATE POLICY "Users can manage own presence"
    ON public.user_presence FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Public can read presence" ON public.user_presence;
CREATE POLICY "Public can read presence"
    ON public.user_presence FOR SELECT
    USING (true);

-- 8. Triggers for updated_at
DROP TRIGGER IF EXISTS tr_user_presence_updated_at ON public.user_presence;
CREATE TRIGGER tr_user_presence_updated_at
    BEFORE UPDATE ON public.user_presence
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- 9. Trigger to create presence row on profile creation
CREATE OR REPLACE FUNCTION public.init_user_presence()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.user_presence (user_id)
    VALUES (NEW.id)
    ON CONFLICT (user_id) DO NOTHING;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS tr_init_user_presence ON public.profiles;
CREATE TRIGGER tr_init_user_presence
    AFTER INSERT ON public.profiles
    FOR EACH ROW EXECUTE FUNCTION public.init_user_presence();

-- 10. Ensure existing profiles have presence rows
INSERT INTO public.user_presence (user_id)
SELECT id FROM public.profiles
ON CONFLICT (user_id) DO NOTHING;

-- 11. Trigger to update profile_stats on tip received (placeholder for future)
-- This would be called when tips/payments are implemented