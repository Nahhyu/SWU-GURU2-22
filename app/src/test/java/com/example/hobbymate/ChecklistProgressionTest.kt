package com.example.hobbymate

import com.example.hobbymate.logic.ChecklistProgression
import org.junit.Assert.assertEquals
import org.junit.Test

class ChecklistProgressionTest {

    @Test
    fun `현재 단계만 완료할 수 있다`() {
        assertEquals(2, ChecklistProgression.complete(1, 1, 4))
        assertEquals(1, ChecklistProgression.complete(1, 2, 4))
        assertEquals(1, ChecklistProgression.complete(1, 0, 4))
    }

    @Test
    fun `마지막 단계 이후에는 완료 수가 늘어나지 않는다`() {
        assertEquals(4, ChecklistProgression.complete(4, 4, 4))
    }
}
