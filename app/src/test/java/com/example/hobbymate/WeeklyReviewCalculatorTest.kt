package com.example.hobbymate

import com.example.hobbymate.model.DailyReview
import com.example.hobbymate.ui.review.WeeklyReviewCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeeklyReviewCalculatorTest {

    @Test
    fun `일일 리뷰를 주간 통계로 집계한다`() {
        val reviews = listOf(
            review(minutes = 20, createdAt = DAY_ONE, photoUri = "content://first"),
            review(minutes = 35, createdAt = DAY_TWO, photoUri = "content://latest"),
        )

        val summary = WeeklyReviewCalculator.calculate(reviews, targetSessions = 3)

        assertEquals(2, summary.studiedDays)
        assertEquals(55, summary.totalMinutes)
        assertEquals(27, summary.averageMinutes)
        assertEquals(2, summary.completedSessions)
        assertEquals(66, summary.achievementPercent)
        assertEquals("content://first", summary.firstPhotoUri)
        assertEquals("content://latest", summary.latestPhotoUri)
        assertEquals(2, summary.latestPhotoDay)
    }

    @Test
    fun `사진이 한 장이면 첫 사진에만 표시한다`() {
        val summary = WeeklyReviewCalculator.calculate(
            listOf(review(photoUri = "content://only")),
            targetSessions = 5,
        )

        assertEquals("content://only", summary.firstPhotoUri)
        assertNull(summary.latestPhotoUri)
    }

    private fun review(
        minutes: Int = 10,
        createdAt: Long = DAY_ONE,
        photoUri: String? = null,
    ) = DailyReview(
        sessionId = 1L,
        hobbyId = "climbing",
        weekNumber = 2,
        title = "연습",
        actualMinutes = minutes,
        note = "",
        hardPart = "",
        satisfaction = 4,
        completedSteps = 3,
        totalSteps = 3,
        currentStage = "기초 동작",
        imageUri = photoUri,
        createdAt = createdAt,
    )

    private companion object {
        const val DAY_ONE = 1_700_000_000_000L
        const val DAY_TWO = DAY_ONE + 86_400_000L
    }
}
