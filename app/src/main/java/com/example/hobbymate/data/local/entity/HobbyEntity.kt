package com.example.hobbymate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hobbies")
data class HobbyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val estimatedCost: String,
    val difficulty: Int,
    val supplies: String,
    val environmentScore: Double,
    val socialScore: Double,
    val physicalScore: Double,
    val budgetScore: Double,
    val creativityScore: Double,
)
