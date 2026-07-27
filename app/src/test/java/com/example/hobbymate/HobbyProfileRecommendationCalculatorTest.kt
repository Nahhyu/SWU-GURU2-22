package com.example.hobbymate

import com.example.hobbymate.logic.HobbyProfileRecommendationCalculator
import com.example.hobbymate.model.UserPreference
import org.junit.Assert.assertNotEquals
import org.junit.Test

class HobbyProfileRecommendationCalculatorTest {

    @Test
    fun `사용자 답변이 바뀌면 추천 순서도 바뀐다`() {
        val outdoorActive = HobbyProfileRecommendationCalculator.recommend(
            UserPreference(1.0, 0.5, 1.0, 0.5, 0.1),
        )
        val indoorCreative = HobbyProfileRecommendationCalculator.recommend(
            UserPreference(0.1, 0.2, 0.1, 0.3, 1.0),
        )

        assertNotEquals(
            outdoorActive.first().first.id,
            indoorCreative.first().first.id,
        )
    }
}
