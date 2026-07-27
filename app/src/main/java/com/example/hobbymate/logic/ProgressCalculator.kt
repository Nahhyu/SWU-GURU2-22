package com.example.hobbymate.logic

import com.example.hobbymate.model.DailySession

object ProgressCalculator {

    fun progress(sessions: List<DailySession>): Float {
        if (sessions.isEmpty()) return 0f
        return sessions.count { it.isCompleted }.toFloat() / sessions.size
    }

    fun completedMinutes(sessions: List<DailySession>): Int =
        sessions.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
}
