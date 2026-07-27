package com.example.hobbymate.model

data class ActiveGuideConfig(
    val hobbyId: String,
    val level: String,
    val levelLabel: String,
    val goal: String,
    val durationWeeks: Int,
    val minutesPerSession: Int,
    val sessionsPerWeek: Int,
    val createdAt: Long = System.currentTimeMillis(),
)
