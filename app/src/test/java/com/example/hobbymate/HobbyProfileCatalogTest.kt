package com.example.hobbymate

import com.example.hobbymate.model.HobbyProfileCatalog
import org.junit.Assert.assertEquals
import org.junit.Test

class HobbyProfileCatalogTest {

    @Test
    fun `선택한 취미 id에 맞는 상세 정보를 제공한다`() {
        val drawing = HobbyProfileCatalog.get("drawing")

        assertEquals("드로잉", drawing.displayName)
        assertEquals("어디서나", drawing.requiredPlace)
        assertEquals("스케치북·연필", drawing.supplies)
    }

    @Test
    fun `알 수 없는 취미는 기본 취미로 대체한다`() {
        assertEquals(
            HobbyProfileCatalog.DEFAULT_HOBBY_ID,
            HobbyProfileCatalog.get("unknown").id,
        )
    }
}
