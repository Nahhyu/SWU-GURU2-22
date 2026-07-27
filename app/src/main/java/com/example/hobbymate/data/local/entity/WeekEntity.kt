package com.example.hobbymate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weeks")
data class WeekEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val guideId: Long,
    val weekNumber: Int,
    val theme: String,
)
