package com.example.hobbymate.data.repository

import com.example.hobbymate.data.remote.analysis.VideoAnalysisApi
import com.example.hobbymate.data.remote.analysis.dto.VideoAnalysisRequestDto
import com.example.hobbymate.model.VideoAnalysisRequest
import com.example.hobbymate.model.VideoChecklist
import com.example.hobbymate.model.VideoChecklistStep
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoAnalysisRepository @Inject constructor(
    private val api: VideoAnalysisApi,
) {
    suspend fun analyze(request: VideoAnalysisRequest): VideoChecklist {
        val response = api.analyzeVideo(
            VideoAnalysisRequestDto(
                videoId = request.videoId,
                title = request.title,
                description = request.description,
                thumbnailUrl = request.thumbnailUrl,
                channelName = request.channelName,
                hobbyName = request.hobbyName,
                goal = request.goal,
            ),
        )
        val steps = response.steps
            .filter { it.title.isNotBlank() }
            .take(MAX_STEPS)
            .map {
                VideoChecklistStep(
                    title = it.title,
                    description = it.description,
                    estimatedMinutes = it.estimatedMinutes.coerceAtLeast(1),
                )
            }
        require(steps.size >= MIN_STEPS) {
            "AI가 충분한 체크리스트를 만들지 못했어요."
        }
        return VideoChecklist(
            title = response.title.ifBlank { request.title },
            estimatedMinutes = response.estimatedMinutes
                .takeIf { it > 0 }
                ?: steps.sumOf(VideoChecklistStep::estimatedMinutes),
            steps = steps,
        )
    }

    private companion object {
        const val MIN_STEPS = 3
        const val MAX_STEPS = 5
    }
}
