package com.example.hobbymate

import com.example.hobbymate.logic.ProgressCalculator
import com.example.hobbymate.model.DailySession
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun `완료한 세션 비율을 계산한다`() {
        val sessions = listOf(session(true), session(false), session(true), session(false))

        assertEquals(0.5f, ProgressCalculator.progress(sessions), 0.001f)
    }

    private fun session(completed: Boolean) = DailySession(
        dayNumber = 1,
        title = "",
        description = "",
        estimatedMinutes = 30,
        checklist = emptyList(),
        isCompleted = completed,
    )
}
