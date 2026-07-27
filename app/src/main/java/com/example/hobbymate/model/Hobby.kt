package com.example.hobbymate.model

data class Hobby(
    val id: String,
    val name: String,
    val category: HobbyCategory,
    val description: String,
    val estimatedCost: String,
    val difficulty: Int,
    val supplies: List<String>,
    val preferenceVector: UserPreference,
)
