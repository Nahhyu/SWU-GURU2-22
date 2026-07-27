package com.example.hobbymate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_reviews")
data class WeeklyReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val guideId: Long,
    val weekNumber: Int,
    val studiedDays: Int,
    val totalMinutes: Int,
    val completedSessions: Int,
    val totalSessions: Int,
)
