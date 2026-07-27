package com.example.hobbymate.model

data class DailyReview(
    val sessionId: Long,
    val hobbyId: String,
    val weekNumber: Int,
    val title: String,
    val actualMinutes: Int,
    val note: String,
    val hardPart: String,
    val satisfaction: Int,
    val completedSteps: Int,
    val totalSteps: Int,
    val currentStage: String,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
