package com.example.hobbymate.model

data class DailySession(
    val id: Long = 0,
    val dayNumber: Int,
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
    val checklist: List<String>,
    val isCompleted: Boolean = false,
)
