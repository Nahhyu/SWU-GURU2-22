package com.example.hobbymate.model

data class WeeklyReview(
    val guideId: Long,
    val weekNumber: Int,
    val studiedDays: Int,
    val totalMinutes: Int,
    val completedSessions: Int,
    val totalSessions: Int,
)
