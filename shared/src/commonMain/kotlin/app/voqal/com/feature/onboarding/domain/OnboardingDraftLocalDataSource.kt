package app.voqal.com.feature.onboarding.domain

interface OnboardingDraftLocalDataSource {
    suspend fun getDraft(): OnboardingDraft
    suspend fun saveDraft(draft: OnboardingDraft)
    suspend fun clearDraft()
}
