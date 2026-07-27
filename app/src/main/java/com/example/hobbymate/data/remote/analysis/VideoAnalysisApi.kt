package com.example.hobbymate.data.remote.analysis

import com.example.hobbymate.data.remote.analysis.dto.VideoAnalysisRequestDto
import com.example.hobbymate.data.remote.analysis.dto.VideoAnalysisResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface VideoAnalysisApi {

    @POST("v1/analyze-video")
    suspend fun analyzeVideo(
        @Body request: VideoAnalysisRequestDto,
    ): VideoAnalysisResponseDto
}
