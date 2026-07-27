package com.example.hobbymate.ui.guide

import com.example.hobbymate.model.VideoChecklist

data class DailyGuideUiState(
    val isAnalyzing: Boolean = false,
    val checklist: VideoChecklist? = null,
    val completedStepCount: Int = 0,
    val errorMessage: String? = null,
) {
    val isComplete: Boolean
        get() = checklist?.steps?.isNotEmpty() == true &&
            completedStepCount == checklist.steps.size
}
