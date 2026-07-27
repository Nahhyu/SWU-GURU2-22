package com.example.hobbymate.data.repository

import com.example.hobbymate.data.local.dao.ReviewDao
import com.example.hobbymate.data.local.entity.DailyReviewEntity
import com.example.hobbymate.model.DailyReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val reviewDao: ReviewDao,
) {
    suspend fun saveDailyReview(review: DailyReview) {
        reviewDao.upsertDailyReview(
            DailyReviewEntity(
                sessionId = review.sessionId,
                hobbyId = review.hobbyId,
                weekNumber = review.weekNumber,
                title = review.title,
                actualMinutes = review.actualMinutes,
                note = review.note,
                hardPart = review.hardPart,
                satisfaction = review.satisfaction,
                completedSteps = review.completedSteps,
                totalSteps = review.totalSteps,
                currentStage = review.currentStage,
                imageUri = review.imageUri,
                createdAt = review.createdAt,
            ),
        )
    }

    fun observeDailyReviews(
        hobbyId: String,
        weekNumber: Int,
    ): Flow<List<DailyReview>> =
        reviewDao.observeDailyReviews(hobbyId, weekNumber).map { reviews ->
            reviews.map { it.toModel() }
        }

    fun observeAllDailyReviews(hobbyId: String): Flow<List<DailyReview>> =
        reviewDao.observeAllDailyReviews(hobbyId).map { reviews ->
            reviews.map { it.toModel() }
        }

    private fun DailyReviewEntity.toModel() = DailyReview(
        sessionId = sessionId,
        hobbyId = hobbyId,
        weekNumber = weekNumber,
        title = title,
        actualMinutes = actualMinutes,
        note = note,
        hardPart = hardPart,
        satisfaction = satisfaction,
        completedSteps = completedSteps,
        totalSteps = totalSteps,
        currentStage = currentStage,
        imageUri = imageUri,
        createdAt = createdAt,
    )
}
