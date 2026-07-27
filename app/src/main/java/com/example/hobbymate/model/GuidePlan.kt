package com.example.hobbymate.model

data class GuidePlan(
    val id: Long = 0,
    val hobbyId: String,
    val title: String,
    val weeks: List<WeekPlan>,
)
