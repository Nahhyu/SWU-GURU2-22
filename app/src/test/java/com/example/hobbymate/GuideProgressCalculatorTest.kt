package com.example.hobbymate

import com.example.hobbymate.logic.GuideProgressCalculator
import com.example.hobbymate.model.DailyReview
import org.junit.Assert.assertEquals
import org.junit.Test

class GuideProgressCalculatorTest {

    @Test
    fun `완료 세션으로 현재 주차와 전체 진행률을 계산한다`() {
        val reviews = listOf(
            completedReview(week = 1),
            completedReview(week = 1),
            completedReview(week = 2),
        )

        val result = GuideProgressCalculator.calculate(
            reviews = reviews,
            durationWeeks = 4,
            sessionsPerWeek = 2,
        )

        assertEquals(2, result.currentWeek)
        assertEquals(1, result.completedWeeks)
        assertEquals(1, result.completedSessionsThisWeek)
        assertEquals(37, result.overallPercent)
    }

    private fun completedReview(week: Int) = DailyReview(
        sessionId = 1L,
        hobbyId = "climbing",
        weekNumber = week,
        title = "연습",
        actualMinutes = 20,
        note = "",
        hardPart = "",
        satisfaction = 4,
        completedSteps = 3,
        totalSteps = 3,
        currentStage = "기초",
    )
}
