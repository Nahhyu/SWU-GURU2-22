package com.example.hobbymate.ui.review

data class WeeklyReviewSummary(
    val studiedDays: Int = 0,
    val totalMinutes: Int = 0,
    val averageMinutes: Int = 0,
    val completedSessions: Int = 0,
    val targetSessions: Int = 1,
    val achievementPercent: Int = 0,
    val firstPhotoUri: String? = null,
    val latestPhotoUri: String? = null,
    val firstPhotoDay: Int = 1,
    val latestPhotoDay: Int = 1,
    val latestStage: String = "",
)
