package com.example.hobbymate

import com.example.hobbymate.logic.RoadmapValidator
import com.example.hobbymate.model.DailySession
import com.example.hobbymate.model.GuidePlan
import com.example.hobbymate.model.GuideRequest
import com.example.hobbymate.model.WeekPlan
import org.junit.Assert.assertTrue
import org.junit.Test

class RoadmapValidatorTest {

    @Test
    fun `요청한 주차와 세션 구성이 맞으면 유효하다`() {
        val request = GuideRequest("drawing", "beginner", "basic", 1, 30, 1)
        val plan = GuidePlan(
            hobbyId = "drawing",
            title = "test",
            weeks = listOf(
                WeekPlan(
                    weekNumber = 1,
                    theme = "line",
                    sessions = listOf(
                        DailySession(
                            dayNumber = 1,
                            title = "line",
                            description = "",
                            estimatedMinutes = 30,
                            checklist = emptyList(),
                        ),
                    ),
                ),
            ),
        )

        assertTrue(RoadmapValidator.validate(plan, request))
    }
}
