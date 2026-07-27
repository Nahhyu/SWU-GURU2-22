package com.example.hobbymate.model

data class YouTubeVideo(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val channelName: String,
    val description: String = "",
    val duration: String = "",
)
