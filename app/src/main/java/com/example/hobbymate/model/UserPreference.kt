package com.example.hobbymate.model

data class UserPreference(
    val environment: Double = 0.0,
    val social: Double = 0.0,
    val physicalIntensity: Double = 0.0,
    val budget: Double = 0.0,
    val structureCreativity: Double = 0.0,
) {
    fun asVector(): List<Double> = listOf(
        environment,
        social,
        physicalIntensity,
        budget,
        structureCreativity,
    )
}
