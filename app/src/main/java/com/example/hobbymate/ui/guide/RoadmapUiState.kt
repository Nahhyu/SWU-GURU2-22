package com.example.hobbymate.ui.guide

import com.example.hobbymate.model.DailyReview

data class RoadmapUiState(
    val reviews: List<DailyReview> = emptyList(),
    val isLoading: Boolean = true,
)
