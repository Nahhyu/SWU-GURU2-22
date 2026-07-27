package com.example.hobbymate

import com.example.hobbymate.model.HobbyVideoTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HobbyVideoTagsTest {

    @Test
    fun `취미 id에 맞는 한국어 검색 태그를 만든다`() {
        val climbing = HobbyVideoTags.get("climbing")

        assertEquals("클라이밍", climbing.displayName)
        assertTrue(climbing.searchQuery.contains("볼더링"))
        assertTrue(climbing.searchQuery.contains("기초"))
    }

    @Test
    fun `지원하지 않는 취미는 기본 취미로 대체한다`() {
        val unknown = HobbyVideoTags.get("unknown")

        assertEquals(HobbyVideoTags.DEFAULT_HOBBY_ID, unknown.id)
    }

    @Test
    fun `화면 표시 이름을 api용 id로 변환한다`() {
        assertEquals("calligraphy", HobbyVideoTags.idForDisplayName("캘리그라피"))
        assertEquals("baking", HobbyVideoTags.idForDisplayName("베이킹"))
    }
}
