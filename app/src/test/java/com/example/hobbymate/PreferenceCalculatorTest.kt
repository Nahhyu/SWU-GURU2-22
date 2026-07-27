package com.example.hobbymate

import com.example.hobbymate.logic.PreferenceCalculator
import com.example.hobbymate.model.PreferenceAnswer
import com.example.hobbymate.model.UserPreference
import org.junit.Assert.assertEquals
import org.junit.Test

class PreferenceCalculatorTest {

    @Test
    fun `답변 가중치의 평균으로 사용자 성향을 계산한다`() {
        val answers = listOf(
            PreferenceAnswer(1, "a", UserPreference(environment = 0.2, social = 0.4)),
            PreferenceAnswer(2, "b", UserPreference(environment = 0.8, social = 0.6)),
        )

        val result = PreferenceCalculator.calculate(answers)

        assertEquals(0.5, result.environment, 0.001)
        assertEquals(0.5, result.social, 0.001)
    }
}
