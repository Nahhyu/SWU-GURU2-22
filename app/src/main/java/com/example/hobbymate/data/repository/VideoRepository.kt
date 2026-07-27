package com.example.hobbymate.data.repository

import com.example.hobbymate.BuildConfig
import com.example.hobbymate.data.remote.youtube.YouTubeApi
import com.example.hobbymate.model.HobbyVideoTags
import com.example.hobbymate.model.YouTubeVideo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val youTubeApi: YouTubeApi,
) {
    suspend fun searchByHobby(
        hobbyId: String,
        maxResults: Int = DEFAULT_MAX_RESULTS,
    ): List<YouTubeVideo> {
        val hobby = HobbyVideoTags.get(hobbyId)
        return search(hobby.searchQuery, maxResults)
    }

    suspend fun search(
        query: String,
        maxResults: Int = DEFAULT_MAX_RESULTS,
    ): List<YouTubeVideo> {
        val apiKey = BuildConfig.YOUTUBE_API_KEY
        if (apiKey.isBlank()) throw MissingYouTubeApiKeyException()

        return youTubeApi.search(
            query = query,
            apiKey = apiKey,
            maxResults = maxResults.coerceIn(1, MAX_RESULTS_LIMIT),
        ).items
            .filter { it.id.videoId.isNotBlank() }
            .distinctBy { it.id.videoId }
            .map { item ->
                YouTubeVideo(
                    videoId = item.id.videoId,
                    title = item.snippet.title,
                    thumbnailUrl = item.snippet.thumbnails.medium.url,
                    channelName = item.snippet.channelTitle,
                    description = item.snippet.description,
                )
            }
    }

    private companion object {
        const val DEFAULT_MAX_RESULTS = 5
        const val MAX_RESULTS_LIMIT = 50
    }
}

class MissingYouTubeApiKeyException : IllegalStateException(
    "local.properties에 YOUTUBE_API_KEY를 설정해 주세요.",
)
