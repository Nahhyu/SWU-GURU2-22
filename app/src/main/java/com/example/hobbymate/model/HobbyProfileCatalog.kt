package com.example.hobbymate.model

data class HobbyProfile(
    val id: String,
    val displayName: String,
    val iconStyle: HobbyIconStyle,
    val estimatedCost: String,
    val requiredPlace: String,
    val difficulty: String,
    val supplies: String,
    val sampleGoal: String,
) {
    val icon: String
        get() = iconStyle.emoji
}

enum class HobbyIconStyle(val emoji: String) {
    RUNNING("🏃‍♀️"),
    MUSIC("🎵"),
    ART("🎨"),
    COOKING("🍳"),
    PHOTOGRAPHY("📷"),
    READING("📖"),
    DANCE("💃"),
    PLANT("🪴"),
    TRAVEL("🌍"),
    YOGA("🧘‍♀️"),
}

object HobbyProfileCatalog {

    private val profiles = listOf(
        HobbyProfile("running", "러닝", HobbyIconStyle.RUNNING, "월 1~5만원", "공원·러닝 트랙", "입문 쉬움", "러닝화", "30분 쉬지 않고 달리기"),
        HobbyProfile("fitness", "헬스", HobbyIconStyle.RUNNING, "월 5~15만원", "헬스장", "입문 보통", "운동복", "기초 전신 루틴 완주하기"),
        HobbyProfile("pilates", "필라테스", HobbyIconStyle.YOGA, "월 10~25만원", "필라테스 스튜디오", "입문 보통", "운동복", "코어 기초 동작 익히기"),
        HobbyProfile("yoga", "요가", HobbyIconStyle.YOGA, "월 3~15만원", "집·요가원", "입문 쉬움", "요가 매트", "기초 자세 10개 익히기"),
        HobbyProfile("swimming", "수영", HobbyIconStyle.RUNNING, "월 5~15만원", "수영장", "입문 보통", "수영복·수경", "자유형 500m 완주하기"),
        HobbyProfile("hiking", "등산", HobbyIconStyle.TRAVEL, "월 3~15만원", "등산로", "입문 쉬움", "등산화", "초급 산 정상 완주하기"),
        HobbyProfile("climbing", "클라이밍", HobbyIconStyle.TRAVEL, "월 8~12만원", "실내 클라이밍짐", "입문 쉬움", "클라이밍화", "3개월 안에 V3 문제 완등하기"),
        HobbyProfile("camping", "캠핑", HobbyIconStyle.TRAVEL, "월 5~30만원", "캠핑장", "입문 쉬움", "텐트·침낭", "첫 1박 캠핑 완성하기"),
        HobbyProfile("cycling", "자전거", HobbyIconStyle.TRAVEL, "월 3~20만원", "자전거 도로", "입문 쉬움", "자전거·헬멧", "20km 라이딩 완주하기"),
        HobbyProfile("golf", "골프", HobbyIconStyle.TRAVEL, "월 15~40만원", "연습장·골프장", "입문 어려움", "골프채·장갑", "기초 스윙 안정시키기"),
        HobbyProfile("guitar", "기타", HobbyIconStyle.MUSIC, "월 2~10만원", "집·연습실", "입문 보통", "기타·피크", "좋아하는 곡 한 곡 연주하기"),
        HobbyProfile("drums", "드럼", HobbyIconStyle.MUSIC, "월 8~20만원", "음악 연습실", "입문 보통", "드럼 스틱", "기본 8비트 리듬 연주하기"),
        HobbyProfile("piano", "피아노", HobbyIconStyle.MUSIC, "월 5~20만원", "집·피아노 학원", "입문 보통", "피아노·악보", "좋아하는 곡 한 곡 연주하기"),
        HobbyProfile("knitting", "뜨개질", HobbyIconStyle.ART, "월 1~5만원", "어디서나", "입문 쉬움", "실·바늘", "작은 소품 하나 완성하기"),
        HobbyProfile("calligraphy", "캘리그라피", HobbyIconStyle.ART, "월 1~5만원", "집·공방", "입문 쉬움", "펜·연습지", "나만의 문구 작품 완성하기"),
        HobbyProfile("drawing", "드로잉", HobbyIconStyle.ART, "월 1~5만원", "어디서나", "입문 쉬움", "스케치북·연필", "인물 스케치 한 장 완성하기"),
        HobbyProfile("pottery", "도자기", HobbyIconStyle.ART, "월 8~20만원", "도예 공방", "입문 보통", "앞치마", "나만의 컵 하나 완성하기"),
        HobbyProfile("gardening", "식물 키우기", HobbyIconStyle.PLANT, "월 1~5만원", "집·베란다", "입문 쉬움", "화분·흙", "식물 한 달 건강하게 기르기"),
        HobbyProfile("baking", "베이킹", HobbyIconStyle.COOKING, "월 5~15만원", "집·베이킹 공방", "입문 보통", "오븐·계량도구", "디저트 한 종류 완성하기"),
    ).associateBy(HobbyProfile::id)

    fun get(hobbyId: String): HobbyProfile =
        profiles[hobbyId] ?: profiles.getValue(DEFAULT_HOBBY_ID)

    fun all(): List<HobbyProfile> = profiles.values.toList()

    fun categoryLabel(hobbyId: String): String = when (hobbyId) {
        "running", "fitness", "pilates", "yoga", "swimming" -> "운동 / 피트니스"
        "hiking", "climbing", "camping", "cycling", "golf" -> "아웃도어 / 레저"
        "guitar", "drums", "piano" -> "음악"
        "knitting", "calligraphy", "drawing", "pottery" -> "미술 / 공예"
        "gardening" -> "홈 / 라이프스타일"
        "baking" -> "요리 / 베이킹"
        else -> "기타"
    }

    const val DEFAULT_HOBBY_ID = "climbing"
}
