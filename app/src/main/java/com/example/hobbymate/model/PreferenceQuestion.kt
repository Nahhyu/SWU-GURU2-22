package com.example.hobbymate.model

data class PreferenceQuestion(
    val id: Int,
    val question: String,
    val options: List<PreferenceOption>,
)

data class PreferenceOption(
    val id: String,
    val text: String,
    val weights: UserPreference,
)
