package com.example.hobbymate.ui.onboarding

import com.example.hobbymate.model.PreferenceAnswer
import com.example.hobbymate.model.PreferenceQuestion

data class OnboardingUiState(
    val questions: List<PreferenceQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: List<PreferenceAnswer> = emptyList(),
    val isComplete: Boolean = false,
)
