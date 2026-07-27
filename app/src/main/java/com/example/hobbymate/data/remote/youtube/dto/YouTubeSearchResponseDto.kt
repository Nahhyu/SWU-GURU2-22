package com.example.hobbymate.data.remote.youtube.dto

data class YouTubeSearchResponseDto(
    val items: List<YouTubeSearchItemDto> = emptyList(),
)

data class YouTubeSearchItemDto(
    val id: YouTubeIdDto = YouTubeIdDto(),
    val snippet: YouTubeSnippetDto = YouTubeSnippetDto(),
)

data class YouTubeIdDto(
    val videoId: String = "",
)

data class YouTubeSnippetDto(
    val title: String = "",
    val description: String = "",
    val channelTitle: String = "",
    val thumbnails: YouTubeThumbnailsDto = YouTubeThumbnailsDto(),
)

data class YouTubeThumbnailsDto(
    val medium: YouTubeThumbnailDto = YouTubeThumbnailDto(),
)

data class YouTubeThumbnailDto(
    val url: String = "",
)
