-- Phase 2: Social Graph & Profile Stats
-- 1. Create user_follows table
-- 2. Create profile_stats table
-- 3. Add triggers for maintaining stats

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
-- Users can manage their own follows
DROP POLICY IF EXISTS "Users can manage own follows" ON public.user_follows;
CREATE POLICY "Users can manage own follows"
    ON public.user_follows FOR ALL
    TO authenticated
    USING (auth.uid() = follower_id)
    WITH CHECK (auth.uid() = follower_id);

-- Public can view follows (for follower/following lists)
DROP POLICY IF EXISTS "Public can view follows" ON public.user_follows;
CREATE POLICY "Public can view follows"
    ON public.user_follows FOR SELECT
    USING (true);

-- 5. RLS Policies for profile_stats
-- Public read access
DROP POLICY IF EXISTS "Public read profile_stats" ON public.profile_stats;
CREATE POLICY "Public read profile_stats"
    ON public.profile_stats FOR SELECT
    USING (true);

-- Users can update their own stats (via triggers)
DROP POLICY IF EXISTS "Users can update own stats" ON public.profile_stats;
CREATE POLICY "Users can update own stats"
    ON public.profile_stats FOR UPDATE
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- 6. Triggers for maintaining profile_stats

-- Trigger function for user_follows
CREATE OR REPLACE FUNCTION public.update_follow_stats()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- Increment follower's following_count
        UPDATE public.profile_stats
        SET following_count = following_count + 1,
            last_updated = now()
        WHERE user_id = NEW.follower_id;
        
        -- Increment followed's followers_count
        UPDATE public.profile_stats
        SET followers_count = followers_count + 1,
            last_updated = now()
        WHERE user_id = NEW.following_id;
        
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        -- Decrement follower's following_count
        UPDATE public.profile_stats
        SET following_count = following_count - 1,
            last_updated = now()
        WHERE user_id = OLD.follower_id;
        
        -- Decrement followed's followers_count
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