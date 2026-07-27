package com.example.hobbymate

import com.example.hobbymate.logic.RoadmapCurriculum
import com.example.hobbymate.model.HobbyProfileCatalog
import org.junit.Assert.assertTrue
import org.junit.Test

class RoadmapCurriculumTest {

    @Test
    fun `선택한 취미와 목표로 주차 테마를 만든다`() {
        val theme = RoadmapCurriculum.theme(
            hobby = HobbyProfileCatalog.get("drawing"),
            goal = "인물 스케치 완성",
            weekNumber = 2,
            totalWeeks = 4,
        )

        assertTrue(theme.contains("인물 스케치 완성"))
    }
}
