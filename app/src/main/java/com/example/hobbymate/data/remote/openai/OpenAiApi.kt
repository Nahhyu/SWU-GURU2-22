package com.example.hobbymate.data.remote.openai

import com.example.hobbymate.data.remote.openai.dto.GuideGenerationRequestDto
import com.example.hobbymate.data.remote.openai.dto.GuideGenerationResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiApi {

    @POST("v1/responses")
    suspend fun generateGuide(
        @Body request: GuideGenerationRequestDto,
    ): GuideGenerationResponseDto
}
