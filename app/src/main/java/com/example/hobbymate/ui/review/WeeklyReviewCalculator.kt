package com.example.hobbymate.ui.review

import com.example.hobbymate.model.DailyReview
import java.util.Calendar

object WeeklyReviewCalculator {

    fun calculate(
        reviews: List<DailyReview>,
        targetSessions: Int,
    ): WeeklyReviewSummary {
        val safeTargetSessions = targetSessions.coerceAtLeast(1)
        val orderedReviews = reviews.sortedBy(DailyReview::createdAt)
        val studyDayKeys = orderedReviews
            .map { dayKey(it.createdAt) }
            .distinct()
        val completedSessions = orderedReviews.count {
            it.totalSteps > 0 && it.completedSteps >= it.totalSteps
        }
        val photos = orderedReviews.mapNotNull { review ->
            review.imageUri
                ?.takeIf(String::isNotBlank)
                ?.let { uri -> PhotoRecord(uri, dayKey(review.createdAt)) }
        }
        val firstPhoto = photos.firstOrNull()
        val latestPhoto = photos.lastOrNull().takeIf { photos.size > 1 }
        val totalMinutes = orderedReviews.sumOf(DailyReview::actualMinutes)

        return WeeklyReviewSummary(
            studiedDays = studyDayKeys.size,
            totalMinutes = totalMinutes,
            averageMinutes = if (orderedReviews.isEmpty()) {
                0
            } else {
                totalMinutes / orderedReviews.size
            },
            completedSessions = completedSessions,
            targetSessions = safeTargetSessions,
            achievementPercent =
                (completedSessions * 100 / safeTargetSessions).coerceIn(0, 100),
            firstPhotoUri = firstPhoto?.uri,
            latestPhotoUri = latestPhoto?.uri,
            firstPhotoDay = firstPhoto?.let {
                studyDayKeys.indexOf(it.dayKey) + 1
            }?.coerceAtLeast(1) ?: 1,
            latestPhotoDay = latestPhoto?.let {
                studyDayKeys.indexOf(it.dayKey) + 1
            }?.coerceAtLeast(1) ?: studyDayKeys.size.coerceAtLeast(1),
            latestStage = orderedReviews.lastOrNull()?.currentStage.orEmpty(),
        )
    }

    private fun dayKey(createdAt: Long): Int = Calendar.getInstance().run {
        timeInMillis = createdAt
        get(Calendar.YEAR) * DAYS_PER_YEAR + get(Calendar.DAY_OF_YEAR)
    }

    private data class PhotoRecord(
        val uri: String,
        val dayKey: Int,
    )

    private const val DAYS_PER_YEAR = 1_000
}
