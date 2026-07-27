package com.example.hobbymate.logic

import com.example.hobbymate.model.HobbyProfile
import com.example.hobbymate.model.HobbyProfileCatalog
import com.example.hobbymate.model.UserPreference
import kotlin.math.roundToInt

object HobbyProfileRecommendationCalculator {

    fun recommend(
        preference: UserPreference,
        limit: Int = 5,
    ): List<Pair<HobbyProfile, Int>> = HobbyProfileCatalog.all()
        .map { profile ->
            val score = HobbyRecommendationCalculator.cosineSimilarity(
                preference,
                vectorFor(profile.id),
            )
            profile to (score * 100).roundToInt().coerceIn(0, 100)
        }
        .sortedByDescending { it.second }
        .take(limit)

    private fun vectorFor(hobbyId: String): UserPreference = when (hobbyId) {
        "running" -> UserPreference(1.0, 0.4, 1.0, 0.3, 0.1)
        "fitness" -> UserPreference(0.1, 0.4, 1.0, 0.6, 0.2)
        "pilates" -> UserPreference(0.1, 0.6, 0.6, 0.7, 0.3)
        "yoga" -> UserPreference(0.2, 0.3, 0.5, 0.3, 0.5)
        "swimming" -> UserPreference(0.2, 0.3, 0.9, 0.5, 0.2)
        "hiking" -> UserPreference(1.0, 0.5, 0.8, 0.3, 0.2)
        "climbing" -> UserPreference(0.5, 0.6, 0.9, 0.5, 0.5)
        "camping" -> UserPreference(1.0, 0.7, 0.3, 0.8, 0.5)
        "cycling" -> UserPreference(0.9, 0.4, 0.9, 0.7, 0.2)
        "golf" -> UserPreference(0.8, 0.7, 0.5, 1.0, 0.3)
        "guitar" -> UserPreference(0.1, 0.4, 0.2, 0.5, 0.8)
        "drums" -> UserPreference(0.1, 0.5, 0.7, 0.6, 0.7)
        "piano" -> UserPreference(0.1, 0.3, 0.2, 0.7, 0.8)
        "knitting" -> UserPreference(0.1, 0.2, 0.1, 0.2, 0.8)
        "calligraphy" -> UserPreference(0.1, 0.2, 0.1, 0.2, 1.0)
        "drawing" -> UserPreference(0.2, 0.2, 0.1, 0.3, 1.0)
        "pottery" -> UserPreference(0.2, 0.6, 0.3, 0.7, 1.0)
        "gardening" -> UserPreference(0.4, 0.2, 0.3, 0.4, 0.7)
        "baking" -> UserPreference(0.1, 0.5, 0.3, 0.6, 0.8)
        else -> UserPreference(0.5, 0.5, 0.5, 0.5, 0.5)
    }
}
