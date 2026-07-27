package com.example.hobbymate.logic

object ChecklistProgression {

    fun complete(
        completedCount: Int,
        clickedIndex: Int,
        totalCount: Int,
    ): Int {
        if (totalCount <= 0) return 0
        return if (clickedIndex == completedCount && completedCount < totalCount) {
            completedCount + 1
        } else {
            completedCount.coerceIn(0, totalCount)
        }
    }
}
