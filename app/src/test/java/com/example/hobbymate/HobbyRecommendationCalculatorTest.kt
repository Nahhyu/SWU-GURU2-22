package com.example.hobbymate

import com.example.hobbymate.logic.HobbyRecommendationCalculator
import com.example.hobbymate.model.Hobby
import com.example.hobbymate.model.HobbyCategory
import com.example.hobbymate.model.UserPreference
import org.junit.Assert.assertEquals
import org.junit.Test

class HobbyRecommendationCalculatorTest {

    @Test
    fun `사용자 벡터와 가장 가까운 취미를 먼저 추천한다`() {
        val target = UserPreference(1.0, 0.0, 1.0, 0.0, 0.0)
        val matching = hobby("running", UserPreference(1.0, 0.0, 1.0, 0.0, 0.0))
        val different = hobby("drawing", UserPreference(0.0, 1.0, 0.0, 1.0, 1.0))

        val result = HobbyRecommendationCalculator.recommend(
            target,
            listOf(different, matching),
        )

        assertEquals("running", result.first().first.id)
    }

    private fun hobby(id: String, vector: UserPreference) = Hobby(
        id = id,
        name = id,
        category = HobbyCategory.ETC,
        description = "",
        estimatedCost = "",
        difficulty = 1,
        supplies = emptyList(),
        preferenceVector = vector,
    )
}
