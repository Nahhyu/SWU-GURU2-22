package com.example.hobbymate.navigation

import androidx.annotation.IdRes
import com.example.hobbymate.R

enum class ScreenRoute(@param:IdRes val destinationId: Int) {
    SPLASH(R.id.splashFragment),
    ONBOARDING(R.id.onboardingFragment),
    HOBBY_RECOMMEND(R.id.hobbyRecommendFragment),
    HOBBY_SELECT(R.id.hobbySelectFragment),
    HOBBY_INFO(R.id.hobbyInfoFragment),
    HOBBY_READY(R.id.hobbyReadyFragment),
    GUIDE_SETUP(R.id.guideSetupFragment),
    ROADMAP(R.id.roadmapFragment),
    SESSION_LIST(R.id.sessionListFragment),
    DAILY_GUIDE(R.id.dailyGuideFragment),
    DAILY_REVIEW(R.id.dailyReviewFragment),
    WEEKLY_REVIEW(R.id.weeklyReviewFragment),
    HOME(R.id.homeFragment),
}
