package com.example.hobbymate.logic

import com.example.hobbymate.model.GuidePlan
import com.example.hobbymate.model.GuideRequest
import kotlin.math.abs

object RoadmapValidator {

    fun validate(plan: GuidePlan, request: GuideRequest): Boolean {
        if (plan.weeks.size != request.durationWeeks) return false

        return plan.weeks.all { week ->
            week.sessions.size == request.sessionsPerWeek &&
                week.sessions.all { session ->
                    abs(session.estimatedMinutes - request.minutesPerSession) <= 10
                }
        }
    }
}
