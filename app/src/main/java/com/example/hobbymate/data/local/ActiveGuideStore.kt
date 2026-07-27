package com.example.hobbymate.data.local

import android.content.Context
import com.example.hobbymate.model.ActiveGuideConfig
import com.example.hobbymate.model.GuideRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveGuideStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val _activeGuide = MutableStateFlow(load())
    val activeGuide: StateFlow<ActiveGuideConfig?> = _activeGuide.asStateFlow()

    fun save(request: GuideRequest, levelLabel: String) {
        val config = ActiveGuideConfig(
            hobbyId = request.hobbyId,
            level = request.level,
            levelLabel = levelLabel,
            goal = request.goal,
            durationWeeks = request.durationWeeks,
            minutesPerSession = request.minutesPerSession,
            sessionsPerWeek = request.sessionsPerWeek,
        )
        preferences.edit()
            .putString(KEY_HOBBY_ID, config.hobbyId)
            .putString(KEY_LEVEL, config.level)
            .putString(KEY_LEVEL_LABEL, config.levelLabel)
            .putString(KEY_GOAL, config.goal)
            .putInt(KEY_DURATION_WEEKS, config.durationWeeks)
            .putInt(KEY_MINUTES_PER_SESSION, config.minutesPerSession)
            .putInt(KEY_SESSIONS_PER_WEEK, config.sessionsPerWeek)
            .putLong(KEY_CREATED_AT, config.createdAt)
            .apply()
        _activeGuide.value = config
    }

    fun get(): ActiveGuideConfig? = _activeGuide.value

    private fun load(): ActiveGuideConfig? {
        val hobbyId = preferences.getString(KEY_HOBBY_ID, null) ?: return null
        return ActiveGuideConfig(
            hobbyId = hobbyId,
            level = preferences.getString(KEY_LEVEL, "first") ?: "first",
            levelLabel = preferences.getString(KEY_LEVEL_LABEL, "완전 처음")
                ?: "완전 처음",
            goal = preferences.getString(KEY_GOAL, "").orEmpty(),
            durationWeeks = preferences.getInt(KEY_DURATION_WEEKS, 12),
            minutesPerSession = preferences.getInt(KEY_MINUTES_PER_SESSION, 30),
            sessionsPerWeek = preferences.getInt(KEY_SESSIONS_PER_WEEK, 5),
            createdAt = preferences.getLong(KEY_CREATED_AT, 0L),
        )
    }

    private companion object {
        const val PREFERENCES_NAME = "active_guide"
        const val KEY_HOBBY_ID = "hobby_id"
        const val KEY_LEVEL = "level"
        const val KEY_LEVEL_LABEL = "level_label"
        const val KEY_GOAL = "goal"
        const val KEY_DURATION_WEEKS = "duration_weeks"
        const val KEY_MINUTES_PER_SESSION = "minutes_per_session"
        const val KEY_SESSIONS_PER_WEEK = "sessions_per_week"
        const val KEY_CREATED_AT = "created_at"
    }
}
