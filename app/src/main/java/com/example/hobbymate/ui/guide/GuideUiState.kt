package com.example.hobbymate.ui.guide

import com.example.hobbymate.model.DailySession
import com.example.hobbymate.model.GuidePlan
import com.example.hobbymate.model.GuideRequest
import com.example.hobbymate.model.WeekPlan
import com.example.hobbymate.model.YouTubeVideo

data class GuideUiState(
    val request: GuideRequest? = null,
    val isLoading: Boolean = false,
    val guidePlan: GuidePlan? = null,
    val currentWeek: WeekPlan? = null,
    val currentSession: DailySession? = null,
    val recommendedVideos: List<YouTubeVideo> = emptyList(),
    val errorMessage: String? = null,
)
