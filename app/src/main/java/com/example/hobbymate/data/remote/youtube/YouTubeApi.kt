package com.example.hobbymate.data.remote.youtube

import com.example.hobbymate.data.remote.youtube.dto.YouTubeSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {

    @GET("youtube/v3/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 5,
        @Query("order") order: String = "relevance",
        @Query("relevanceLanguage") relevanceLanguage: String = "ko",
        @Query("regionCode") regionCode: String = "KR",
        @Query("safeSearch") safeSearch: String = "moderate",
        @Query("videoEmbeddable") videoEmbeddable: String = "true",
        @Query("videoSyndicated") videoSyndicated: String = "true",
    ): YouTubeSearchResponseDto
}
