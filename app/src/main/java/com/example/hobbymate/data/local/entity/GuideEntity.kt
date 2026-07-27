package com.example.hobbymate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guides")
data class GuideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hobbyId: String,
    val title: String,
    val goal: String,
    val durationWeeks: Int,
    val createdAt: Long = System.currentTimeMillis(),
)
