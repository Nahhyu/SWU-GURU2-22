package com.example.hobbymate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val environmentScore: Double,
    val socialScore: Double,
    val physicalScore: Double,
    val budgetScore: Double,
    val creativityScore: Double,
    val updatedAt: Long = System.currentTimeMillis(),
)
