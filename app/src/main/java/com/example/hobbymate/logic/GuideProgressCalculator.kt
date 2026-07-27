package com.example.hobbymate.logic

import com.example.hobbymate.model.DailyReview

data class GuideProgressSummary(
    val currentWeek: Int,
    val completedWeeks: Int,
    val completedSessionsThisWeek: Int,
    val completedSessionsTotal: Int,
    val overallPercent: Int,
)

object GuideProgressCalculator {

    fun calculate(
        reviews: List<DailyReview>,
        durationWeeks: Int,
        sessionsPerWeek: Int,
    ): GuideProgressSummary {
        val safeDuration = durationWeeks.coerceAtLeast(1)
        val safeFrequency = sessionsPerWeek.coerceAtLeast(1)
        val completedByWeek = reviews
            .filter { it.totalSteps > 0 && it.completedSteps >= it.totalSteps }
            .groupingBy(DailyReview::weekNumber)
            .eachCount()
        val completedWeeks = (1..safeDuration)
            .takeWhile { (completedByWeek[it] ?: 0) >= safeFrequency }
            .size
        val currentWeek = (completedWeeks + 1).coerceAtMost(safeDuration)
        val completedTotal = completedByWeek.values.sum()
        val totalSessions = safeDuration * safeFrequency

        return GuideProgressSummary(
            currentWeek = currentWeek,
            completedWeeks = completedWeeks,
            completedSessionsThisWeek =
                (completedByWeek[currentWeek] ?: 0).coerceAtMost(safeFrequency),
            completedSessionsTotal = completedTotal,
            overallPercent =
                (completedTotal * 100 / totalSessions).coerceIn(0, 100),
        )
    }
}
