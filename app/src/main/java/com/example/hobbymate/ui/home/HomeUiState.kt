package com.example.hobbymate.ui.home

import com.example.hobbymate.model.ActiveGuideConfig
import com.example.hobbymate.model.DailyReview
import com.example.hobbymate.model.HobbyProfile

data class HomeUiState(
    val activeGuide: ActiveGuideConfig? = null,
    val activeReviews: List<DailyReview> = emptyList(),
    val selectedHobbies: List<HobbyProfile> = emptyList(),
    val recommendations: List<HobbyProfile> = emptyList(),
    val isLoading: Boolean = true,
)
