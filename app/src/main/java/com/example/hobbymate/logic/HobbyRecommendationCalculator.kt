package com.example.hobbymate.logic

import com.example.hobbymate.model.Hobby
import com.example.hobbymate.model.UserPreference
import kotlin.math.sqrt

object HobbyRecommendationCalculator {

    fun recommend(
        preference: UserPreference,
        hobbies: List<Hobby>,
        limit: Int = 5,
    ): List<Pair<Hobby, Double>> = hobbies
        .map { hobby -> hobby to cosineSimilarity(preference, hobby.preferenceVector) }
        .sortedByDescending { it.second }
        .take(limit)

    fun cosineSimilarity(left: UserPreference, right: UserPreference): Double {
        val a = left.asVector()
        val b = right.asVector()
        val dot = a.zip(b).sumOf { (x, y) -> x * y }
        val magnitudeA = sqrt(a.sumOf { it * it })
        val magnitudeB = sqrt(b.sumOf { it * it })
        return if (magnitudeA == 0.0 || magnitudeB == 0.0) {
            0.0
        } else {
            dot / (magnitudeA * magnitudeB)
        }
    }
}
