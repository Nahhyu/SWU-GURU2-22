package com.example.hobbymate.model

data class PreferenceAnswer(
    val questionId: Int,
    val optionId: String,
    val weights: UserPreference,
)
