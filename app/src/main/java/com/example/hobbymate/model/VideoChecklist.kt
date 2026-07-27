package com.example.hobbymate.model

data class VideoAnalysisRequest(
    val videoId: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelName: String,
    val hobbyName: String,
    val goal: String,
)

data class VideoChecklist(
    val title: String,
    val estimatedMinutes: Int,
    val steps: List<VideoChecklistStep>,
)

data class VideoChecklistStep(
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
)
