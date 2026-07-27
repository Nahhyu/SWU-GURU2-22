package com.example.hobbymate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val videoId: String,
    val sessionId: Long,
    val title: String,
    val thumbnailUrl: String,
    val channelName: String,
    val duration: String,
)
