package com.example.hobbymate.model

data class WeekPlan(
    val weekNumber: Int,
    val theme: String,
    val sessions: List<DailySession>,
)
