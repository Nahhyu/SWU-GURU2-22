package com.example.hobbymate.data.repository

import com.example.hobbymate.data.local.dao.GuideDao
import com.example.hobbymate.model.GuideRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuideRepository @Inject constructor(
    private val guideDao: GuideDao,
) {
    suspend fun generateGuide(request: GuideRequest): String {
        return """
            ${request.hobbyId} 초보자를 위한 ${request.durationWeeks}주 가이드를 만들어 주세요.
            목표: ${request.goal}
            주 ${request.sessionsPerWeek}회, 회당 ${request.minutesPerSession}분
        """.trimIndent()
    }

    suspend fun completeSession(sessionId: Long) {
        guideDao.completeSession(sessionId)
    }
}
