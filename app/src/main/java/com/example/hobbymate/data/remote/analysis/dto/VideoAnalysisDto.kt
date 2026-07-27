package com.example.hobbymate.data.remote.analysis.dto

data class VideoAnalysisRequestDto(
    val videoId: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelName: String,
    val hobbyName: String,
    val goal: String,
)

data class VideoAnalysisResponseDto(
    val title: String = "",
    val estimatedMinutes: Int = 0,
    val steps: List<VideoChecklistStepDto> = emptyList(),
)

data class VideoChecklistStepDto(
    val title: String = "",
    val description: String = "",
    val estimatedMinutes: Int = 0,
)
