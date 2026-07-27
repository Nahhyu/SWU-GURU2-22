package com.example.hobbymate.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hobbymate.data.local.entity.DailyReviewEntity
import com.example.hobbymate.data.local.entity.WeeklyReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query(
        "SELECT * FROM daily_reviews " +
            "WHERE hobbyId = :hobbyId AND weekNumber = :weekNumber " +
            "ORDER BY createdAt ASC",
    )
    fun observeDailyReviews(
        hobbyId: String,
        weekNumber: Int,
    ): Flow<List<DailyReviewEntity>>

    @Query(
        "SELECT * FROM daily_reviews WHERE hobbyId = :hobbyId ORDER BY createdAt ASC",
    )
    fun observeAllDailyReviews(hobbyId: String): Flow<List<DailyReviewEntity>>

    @Query(
        "SELECT * FROM weekly_reviews " +
            "WHERE guideId = :guideId AND weekNumber = :weekNumber LIMIT 1",
    )
    fun observeWeeklyReview(guideId: Long, weekNumber: Int): Flow<WeeklyReviewEntity?>

    @Upsert
    suspend fun upsertDailyReview(review: DailyReviewEntity)

    @Upsert
    suspend fun upsertWeeklyReview(review: WeeklyReviewEntity)
}
