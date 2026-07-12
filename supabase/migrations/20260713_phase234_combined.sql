-- Combined Migration: Phases 2, 3, 4
-- Run this to add missing tables: user_follows, profile_stats, notifications, etc.

-- ============================================================
-- PHASE 2: Social Graph & Profile Stats
-- ============================================================

-- 1. Create user_follows table
CREATE TABLE IF NOT EXISTS public.user_follows (
    follower_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    following_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (follower_id, following_id)
);

-- Prevent self-follow
ALTER TABLE public.user_follows
ADD CONSTRAINT no_self_follow CHECK (follower_id != following_id);

-- Indexes for efficient lookups
CREATE INDEX IF NOT EXISTS idx_user_follows_following ON public.user_follows(following_id);
CREATE INDEX IF NOT EXISTS idx_user_follows_follower ON public.user_follows(follower_id);

-- 2. Create profile_stats table
CREATE TABLE IF NOT EXISTS public.profile_stats (
    user_id UUID PRIMARY KEY REFERENCES public.profiles(id) ON DELETE CASCADE,
    followers_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    rooms_joined INT DEFAULT 0,
    rooms_hosted INT DEFAULT 0,
    rooms_created INT DEFAULT 0,
    likes_received INT DEFAULT 0,
    last_updated TIMESTAMPTZ DEFAULT now()
);

-- 3. Enable RLS
ALTER TABLE public.user_follows ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.profile_stats ENABLE ROW LEVEL SECURITY;

-- 4. RLS Policies for user_follows
DROP POLICY IF EXISTS "Users can manage own follows" ON public.user_follows;
CREATE POLICY "Users can manage own follows"
    ON public.user_follows FOR ALL
    TO authenticated
    USING (auth.uid() = follower_id)
    WITH CHECK (auth.uid() = follower_id);

DROP POLICY IF EXISTS "Public can view follows" ON public.user_follows;
CREATE POLICY "Public can view follows"
    ON public.user_follows FOR SELECT
    USING (true);

-- 5. RLS Policies for profile_stats
DROP POLICY IF EXISTS "Public read profile_stats" ON public.profile_stats;
CREATE POLICY "Public read profile_stats"
    ON public.profile_stats FOR SELECT
    USING (true);

DROP POLICY IF EXISTS "Users can update own stats" ON public.profile_stats;
CREATE POLICY "Users can update own stats"
    ON public.profile_stats FOR UPDATE
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- 6. Triggers for maintaining profile_stats
CREATE OR REPLACE FUNCTION public.update_follow_stats()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE public.profile_stats
        SET following_count = following_count + 1,
            last_updated = now()
        WHERE user_id = NEW.follower_id;
        
        UPDATE public.profile_stats
        SET followers_count = followers_count + 1,
            last_updated = now()
        WHERE user_id = NEW.following_id;
        
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE public.profile_stats
        SET following_count = following_count - 1,
            last_updated = now()
        WHERE user_id = OLD.follower_id;
        
        UPDATE public.profile_stats
        SET followers_count = followers_count - 1,
            last_updated = now()
        WHERE user_id = OLD.following_id;
        
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS tr_user_follows_stats ON public.user_follows;
CREATE TRIGGER tr_user_follows_stats
    AFTER INSERT OR DELETE ON public.user_follows
    FOR EACH ROW EXECUTE FUNCTION public.update_follow_stats();

-- 7. Trigger for profile creation - initialize stats
CREATE OR REPLACE FUNCTION public.init_profile_stats()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profile_stats (user_id)
    VALUES (NEW.id)
    ON CONFLICT (user_id) DO NOTHING;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS tr_init_profile_stats ON public.profiles;
CREATE TRIGGER tr_init_profile_stats
    AFTER INSERT ON public.profiles
    FOR EACH ROW EXECUTE FUNCTION public.init_profile_stats();

-- 8. Ensure existing profiles have stats entries
INSERT INTO public.profile_stats (user_id)
SELECT id FROM public.profiles
ON CONFLICT (user_id) DO NOTHING;

-- ============================================================
-- PHASE 3: Room System Enhancement
-- ============================================================

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

-- 6. Create saved_rooms table
CREATE TABLE IF NOT EXISTS public.saved_rooms (
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    room_id UUID NOT NULL REFERENCES public.rooms(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (user_id, room_id)
);

CREATE INDEX IF NOT EXISTS idx_saved_rooms_user ON public.saved_rooms(user_id, created_at DESC);

-- 7. Enable RLS
ALTER TABLE public.user_room_history ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.room_invites ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.room_reminders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.saved_rooms ENABLE ROW LEVEL SECURITY;

-- 8. RLS Policies for user_room_history
DROP POLICY IF EXISTS "Users can view own room history" ON public.user_room_history;
CREATE POLICY "Users can view own room history"
    ON public.user_room_history FOR SELECT
    TO authenticated
    USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Public can view room history" ON public.user_room_history;
CREATE POLICY "Public can view room history"
    ON public.user_room_history FOR SELECT
    USING (true);

DROP POLICY IF EXISTS "System can insert room history" ON public.user_room_history;
CREATE POLICY "System can insert room history"
    ON public.user_room_history FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = user_id);

-- 9. RLS Policies for room_invites
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

-- 10. RLS Policies for room_reminders
DROP POLICY IF EXISTS "Users can manage own reminders" ON public.room_reminders;
CREATE POLICY "Users can manage own reminders"
    ON public.room_reminders FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- 11. RLS Policies for saved_rooms
DROP POLICY IF EXISTS "Users can manage own saved rooms" ON public.saved_rooms;
CREATE POLICY "Users can manage own saved rooms"
    ON public.saved_rooms FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- 12. Triggers for updating user_room_history on room_sessions changes
CREATE OR REPLACE FUNCTION public.sync_user_room_history()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO public.user_room_history (user_id, room_id, role, joined_at)
        VALUES (NEW.user_id, NEW.room_id, NEW.role, NEW.joined_at)
        ON CONFLICT DO NOTHING;
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' AND NEW.left_at IS NOT NULL AND OLD.left_at IS NULL THEN
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

-- 13. Trigger for updating profile_stats on room join
CREATE OR REPLACE FUNCTION public.update_room_stats()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE public.profile_stats
        SET rooms_joined = rooms_joined + 1,
            last_updated = now()
        WHERE user_id = NEW.user_id;
        
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

-- 14. Trigger for rooms_created stat on room creation
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

-- ============================================================
-- PHASE 4: Safety & Engagement
-- ============================================================

-- 1. user_blocks
CREATE TABLE IF NOT EXISTS public.user_blocks (
    blocker_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (blocker_id, blocked_id)
);

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
    entity_id UUID,
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

-- 7. RLS Policies for user_blocks
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

-- 8. RLS Policies for user_mutes
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

-- 9. RLS Policies for user_reports
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

-- 10. RLS Policies for notifications
DROP POLICY IF EXISTS "Users can manage own notifications" ON public.notifications;
CREATE POLICY "Users can manage own notifications"
    ON public.notifications FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- 11. RLS Policies for user_presence
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

-- 12. Triggers for updated_at
DROP TRIGGER IF EXISTS tr_user_presence_updated_at ON public.user_presence;
CREATE TRIGGER tr_user_presence_updated_at
    BEFORE UPDATE ON public.user_presence
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- 13. Trigger to create presence row on profile creation
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

-- 14. Ensure existing profiles have presence rows
INSERT INTO public.user_presence (user_id)
SELECT id FROM public.profiles
ON CONFLICT (user_id) DO NOTHING;
