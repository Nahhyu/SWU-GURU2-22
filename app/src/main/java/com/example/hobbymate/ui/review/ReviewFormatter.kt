package com.example.hobbymate.ui.review

object ReviewFormatter {

    fun duration(minutes: Int): String {
        val safeMinutes = minutes.coerceAtLeast(0)
        val hours = safeMinutes / 60
        val remainingMinutes = safeMinutes % 60
        return when {
            hours == 0 -> "${safeMinutes}분"
            remainingMinutes == 0 -> "${hours}시간"
            else -> "${hours}시간 ${remainingMinutes}분"
        }
    }

    fun progressPercent(completedSteps: Int, totalSteps: Int): Int {
        if (totalSteps <= 0) return 0
        return completedSteps.coerceIn(0, totalSteps) * 100 / totalSteps
    }
}
