package com.example.hobbymate.model

data class GuideRequest(
    val hobbyId: String,
    val level: String,
    val goal: String,
    val durationWeeks: Int,
    val minutesPerSession: Int,
    val sessionsPerWeek: Int,
)
