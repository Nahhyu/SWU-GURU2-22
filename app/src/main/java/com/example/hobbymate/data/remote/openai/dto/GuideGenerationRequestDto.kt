package com.example.hobbymate.data.remote.openai.dto

data class GuideGenerationRequestDto(
    val model: String = "gpt-5-mini",
    val input: String,
)
