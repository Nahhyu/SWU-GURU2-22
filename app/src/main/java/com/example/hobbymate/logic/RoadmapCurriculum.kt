package com.example.hobbymate.logic

import com.example.hobbymate.model.HobbyProfile

object RoadmapCurriculum {

    fun theme(
        hobby: HobbyProfile,
        goal: String,
        weekNumber: Int,
        totalWeeks: Int,
    ): String = when {
        weekNumber <= 1 -> "${hobby.displayName} 준비와 핵심 기초 익히기"
        weekNumber >= totalWeeks -> "$goal 최종 점검하기"
        else -> {
            val phaseIndex = (
                (weekNumber - 1) * PHASES.size /
                    (totalWeeks - 1).coerceAtLeast(1)
                ).coerceIn(0, PHASES.lastIndex)
            "$goal · ${PHASES[phaseIndex]}"
        }
    }

    fun sessionTitle(theme: String, dayNumber: Int): String =
        when (dayNumber) {
            1 -> "$theme 개념 익히기"
            2 -> "$theme 기본 연습"
            else -> "$theme 반복 실습 ${dayNumber - 1}"
        }

    private val PHASES = listOf(
        "기초 동작 안정화",
        "핵심 기술 반복",
        "응용 동작 연결",
        "실전 완성도 높이기",
    )
}
