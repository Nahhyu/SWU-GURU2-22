package com.example.hobbymate.ui.review

data class WeeklyReviewUiState(
    val summary: WeeklyReviewSummary = WeeklyReviewSummary(),
    val isLoading: Boolean = true,
)
