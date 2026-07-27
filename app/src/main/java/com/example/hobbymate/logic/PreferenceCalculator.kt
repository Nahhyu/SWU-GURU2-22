package com.example.hobbymate.logic

import com.example.hobbymate.model.PreferenceAnswer
import com.example.hobbymate.model.UserPreference

object PreferenceCalculator {

    fun calculate(answers: List<PreferenceAnswer>): UserPreference {
        if (answers.isEmpty()) return UserPreference()

        val size = answers.size.toDouble()
        return UserPreference(
            environment = answers.sumOf { it.weights.environment } / size,
            social = answers.sumOf { it.weights.social } / size,
            physicalIntensity = answers.sumOf { it.weights.physicalIntensity } / size,
            budget = answers.sumOf { it.weights.budget } / size,
            structureCreativity = answers.sumOf { it.weights.structureCreativity } / size,
        )
    }
}
