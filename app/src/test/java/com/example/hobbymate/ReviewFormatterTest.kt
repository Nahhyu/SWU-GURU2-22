package com.example.hobbymate

import com.example.hobbymate.ui.review.ReviewFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewFormatterTest {

    @Test
    fun `소요 시간을 분과 시간 단위로 표시한다`() {
        assertEquals("0분", ReviewFormatter.duration(0))
        assertEquals("30분", ReviewFormatter.duration(30))
        assertEquals("1시간", ReviewFormatter.duration(60))
        assertEquals("1시간 30분", ReviewFormatter.duration(90))
    }

    @Test
    fun `완료 단계의 비율을 퍼센트로 계산한다`() {
        assertEquals(66, ReviewFormatter.progressPercent(2, 3))
        assertEquals(100, ReviewFormatter.progressPercent(5, 3))
        assertEquals(0, ReviewFormatter.progressPercent(1, 0))
    }
}
