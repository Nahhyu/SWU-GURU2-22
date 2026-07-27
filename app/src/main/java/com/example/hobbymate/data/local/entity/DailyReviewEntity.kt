package com.example.hobbymate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reviews")
data class DailyReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val hobbyId: String,
    val weekNumber: Int,
    val title: String,
    val actualMinutes: Int,
    val note: String,
    val hardPart: String,
    val satisfaction: Int,
    val completedSteps: Int,
    val totalSteps: Int,
    val currentStage: String,
    val imageUri: String?,
    val createdAt: Long,
)
