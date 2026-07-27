package com.example.hobbymate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekId: Long,
    val dayNumber: Int,
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
    val checklist: String,
    val isCompleted: Boolean = false,
)
