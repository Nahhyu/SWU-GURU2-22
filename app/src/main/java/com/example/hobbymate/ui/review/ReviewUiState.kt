package com.example.hobbymate.ui.review

import com.example.hobbymate.model.DailyReview
import com.example.hobbymate.model.WeeklyReview

data class ReviewUiState(
    val dailyReview: DailyReview? = null,
    val weeklyReview: WeeklyReview? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
)
