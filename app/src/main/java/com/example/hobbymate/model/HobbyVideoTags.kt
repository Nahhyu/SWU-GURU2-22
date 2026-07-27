package com.example.hobbymate.model

data class HobbyVideoTag(
    val id: String,
    val displayName: String,
    val searchTags: List<String>,
) {
    val searchQuery: String
        get() = searchTags.joinToString(separator = " ")
}

object HobbyVideoTags {

    private val hobbies = listOf(
        HobbyVideoTag("running", "러닝", listOf("러닝", "초보", "달리기 자세", "기초 훈련")),
        HobbyVideoTag("fitness", "헬스", listOf("헬스", "초보", "운동 자세", "기초 루틴")),
        HobbyVideoTag("pilates", "필라테스", listOf("필라테스", "초보", "기초 동작")),
        HobbyVideoTag("yoga", "요가", listOf("요가", "초보", "기초 자세")),
        HobbyVideoTag("swimming", "수영", listOf("수영", "초보", "기초 영법")),
        HobbyVideoTag("hiking", "등산", listOf("등산", "초보", "기초", "안전")),
        HobbyVideoTag("climbing", "클라이밍", listOf("클라이밍", "초보", "볼더링", "기초 자세")),
        HobbyVideoTag("camping", "캠핑", listOf("캠핑", "초보", "장비", "기초")),
        HobbyVideoTag("cycling", "자전거", listOf("자전거", "초보", "라이딩", "기초")),
        HobbyVideoTag("golf", "골프", listOf("골프", "초보", "스윙", "기초")),
        HobbyVideoTag("guitar", "기타", listOf("기타", "초보", "코드", "기초 연습")),
        HobbyVideoTag("drums", "드럼", listOf("드럼", "초보", "기초 연습")),
        HobbyVideoTag("piano", "피아노", listOf("피아노", "초보", "기초 연습")),
        HobbyVideoTag("knitting", "뜨개질", listOf("뜨개질", "초보", "기초", "뜨는 법")),
        HobbyVideoTag("calligraphy", "캘리그라피", listOf("캘리그라피", "초보", "기초", "글씨 쓰기")),
        HobbyVideoTag("drawing", "드로잉", listOf("드로잉", "초보", "스케치", "기초")),
        HobbyVideoTag("pottery", "도자기", listOf("도자기", "초보", "기초", "만들기")),
        HobbyVideoTag("gardening", "식물 키우기", listOf("식물 키우기", "초보", "관리법")),
        HobbyVideoTag("baking", "베이킹", listOf("베이킹", "초보", "기초", "레시피")),
    ).associateBy(HobbyVideoTag::id)

    fun get(hobbyId: String): HobbyVideoTag =
        hobbies[hobbyId] ?: hobbies.getValue(DEFAULT_HOBBY_ID)

    fun idForDisplayName(displayName: String): String =
        hobbies.values.firstOrNull { it.displayName == displayName }?.id
            ?: DEFAULT_HOBBY_ID

    const val DEFAULT_HOBBY_ID = "climbing"
}
