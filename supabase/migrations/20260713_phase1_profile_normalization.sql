-- Phase 1: Core Profile Normalization
-- 1. Add missing columns to profiles table
-- 2. Create profile_social_links table
-- 3. Create profile_interests junction table
-- 4. Create interests table if not exists (reference data)

-- 1. Add missing columns to profiles
ALTER TABLE public.profiles
ADD COLUMN IF NOT EXISTS bio TEXT,
ADD COLUMN IF NOT EXISTS occupation TEXT,
ADD COLUMN IF NOT EXISTS website TEXT,
ADD COLUMN IF NOT EXISTS country_code TEXT,
ADD COLUMN IF NOT EXISTS birthday DATE,
ADD COLUMN IF NOT EXISTS allow_following BOOLEAN DEFAULT true,
ADD COLUMN IF NOT EXISTS allow_dm BOOLEAN DEFAULT true,
ADD COLUMN IF NOT EXISTS show_online BOOLEAN DEFAULT true,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT now();

-- 2. Create profile_social_links table
CREATE TABLE IF NOT EXISTS public.profile_social_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    platform TEXT NOT NULL CHECK (platform IN ('instagram', 'twitter', 'website', 'whatsapp', 'discord', 'telegram', 'tiktok', 'threads', 'custom')),
    url TEXT NOT NULL,
    username TEXT, -- optional handle/username
    visibility TEXT NOT NULL DEFAULT 'public' CHECK (visibility IN ('public', 'followers', 'private')),
    verified BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Index for efficient lookups
CREATE INDEX IF NOT EXISTS idx_profile_social_links_user_id ON public.profile_social_links(user_id);

-- 3. Create profile_interests junction table
CREATE TABLE IF NOT EXISTS public.profile_interests (
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    interest_id TEXT NOT NULL REFERENCES public.interests(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (user_id, interest_id)
);

-- 4. Ensure interests table exists (reference data for onboarding)
CREATE TABLE IF NOT EXISTS public.interests (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    emoji TEXT,
    category TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS public.interest_categories (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    display_order INT DEFAULT 0
);

-- 5. Enable RLS on new tables
ALTER TABLE public.profile_social_links ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.profile_interests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.interests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.interest_categories ENABLE ROW LEVEL SECURITY;

-- 6. RLS Policies

-- profile_social_links: users can manage their own links, public can see public links
DROP POLICY IF EXISTS "Users can manage own social links" ON public.profile_social_links;
CREATE POLICY "Users can manage own social links"
    ON public.profile_social_links FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Public can view public social links" ON public.profile_social_links;
CREATE POLICY "Public can view public social links"
    ON public.profile_social_links FOR SELECT
    USING (visibility = 'public');

-- profile_interests: users can manage their own interests, public can view
DROP POLICY IF EXISTS "Users can manage own interests" ON public.profile_interests;
CREATE POLICY "Users can manage own interests"
    ON public.profile_interests FOR ALL
    TO authenticated
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Public can view interests" ON public.profile_interests;
CREATE POLICY "Public can view interests"
    ON public.profile_interests FOR SELECT
    USING (true);

-- interests and interest_categories: public read access
DROP POLICY IF EXISTS "Public read interests" ON public.interests;
CREATE POLICY "Public read interests"
    ON public.interests FOR SELECT
    USING (true);

DROP POLICY IF EXISTS "Public read interest_categories" ON public.interest_categories;
CREATE POLICY "Public read interest_categories"
    ON public.interest_categories FOR SELECT
    USING (true);

-- 7. Add updated_at trigger for profiles
DROP TRIGGER IF EXISTS tr_profiles_updated_at ON public.profiles;
CREATE TRIGGER tr_profiles_updated_at
    BEFORE UPDATE ON public.profiles
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- 8. Add updated_at trigger for profile_social_links
CREATE OR REPLACE FUNCTION public.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS tr_profile_social_links_updated_at ON public.profile_social_links;
CREATE TRIGGER tr_profile_social_links_updated_at
    BEFORE UPDATE ON public.profile_social_links
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- 9. Seed interest categories
INSERT INTO public.interest_categories (id, name, display_order) VALUES
    ('tech', 'Tech & Innovation', 1),
    ('business', 'Business & Money', 2),
    ('entertainment', 'Entertainment & Arts', 3),
    ('health', 'Health & Mindset', 4),
    ('lifestyle', 'Lifestyle & Culture', 5),
    ('deep', 'Deep Conversations', 6)
ON CONFLICT (id) DO NOTHING;

-- 10. Seed interests
INSERT INTO public.interests (id, name, emoji, category) VALUES
    ('ai_futurism', 'AI & Futurism', '🤖', 'tech'),
    ('startups_vc', 'Startups & VC', '🚀', 'tech'),
    ('coding_dev', 'Coding & Dev', '💻', 'tech'),
    ('crypto_web3', 'Crypto & Web3', '🪙', 'tech'),
    ('gadgets_gear', 'Gadgets & Gear', '🔌', 'tech'),
    ('investing_stocks', 'Investing & Stocks', '📈', 'business'),
    ('marketing_growth', 'Marketing & Growth', '📣', 'business'),
    ('side_hustles', 'Side Hustles', '💼', 'business'),
    ('real_estate', 'Real Estate', '🏢', 'business'),
    ('creator_economy', 'Creator Economy', '📸', 'business'),
    ('music_beats', 'Music & Beats', '🎵', 'entertainment'),
    ('movies_shows', 'Movies & Shows', '🎬', 'entertainment'),
    ('gaming_esports', 'Gaming & Esports', '🎮', 'entertainment'),
    ('anime_manga', 'Anime & Manga', '🎏', 'entertainment'),
    ('standup_comedy', 'Standup Comedy', '🎤', 'entertainment'),
    ('mental_health', 'Mental Health', '🧠', 'health'),
    ('fitness_gym', 'Fitness & Gym', '💪', 'health'),
    ('meditation_zen', 'Meditation & Zen', '🧘', 'health'),
    ('biohacking_diet', 'Biohacking & Diet', '🧪', 'health'),
    ('travel_adventure', 'Travel & Adventure', '✈️', 'lifestyle'),
    ('books_literature', 'Books & Literature', '📚', 'lifestyle'),
    ('food_cooking', 'Food & Cooking', '🍳', 'lifestyle'),
    ('dating_relationships', 'Dating & Relationships', '❤️', 'lifestyle'),
    ('fashion_style', 'Fashion & Style', '✨', 'lifestyle'),
    ('philosophy', 'Philosophy', '🏛️', 'deep'),
    ('true_crime', 'True Crime & Mystery', '🕵️', 'deep'),
    ('history_myths', 'History & Myths', '📜', 'deep'),
    ('current_events', 'Current Events', '🌍', 'deep'),
    ('pop_culture', 'Pop Culture', '🍿', 'deep')
ON CONFLICT (id) DO NOTHING;