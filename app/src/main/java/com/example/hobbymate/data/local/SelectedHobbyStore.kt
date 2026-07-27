package com.example.hobbymate.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedHobbyStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val _selectedHobbyIds = MutableStateFlow(loadAll())
    val selectedHobbyIds: StateFlow<List<String>> = _selectedHobbyIds.asStateFlow()

    fun save(hobbyId: String) {
        val updated = (_selectedHobbyIds.value + hobbyId).distinct()
        persist(updated, hobbyId)
    }

    fun saveAll(hobbyIds: List<String>) {
        val updated = hobbyIds.filter(String::isNotBlank).distinct()
        if (updated.isEmpty()) return
        persist(updated, updated.first())
    }

    fun get(): String =
        preferences.getString(KEY_HOBBY_ID, null)
            ?: _selectedHobbyIds.value.firstOrNull()
            ?: DEFAULT_HOBBY_ID

    fun getAll(): List<String> = _selectedHobbyIds.value

    fun hasSelection(): Boolean = _selectedHobbyIds.value.isNotEmpty()

    private fun persist(hobbyIds: List<String>, primaryHobbyId: String) {
        preferences.edit()
            .putString(KEY_HOBBY_ID, primaryHobbyId)
            .putString(KEY_HOBBY_IDS, hobbyIds.joinToString(SEPARATOR))
            .apply()
        _selectedHobbyIds.value = hobbyIds
    }

    private fun loadAll(): List<String> {
        val savedIds = preferences.getString(KEY_HOBBY_IDS, null)
            ?.split(SEPARATOR)
            ?.filter(String::isNotBlank)
            .orEmpty()
        if (savedIds.isNotEmpty()) return savedIds
        return listOfNotNull(preferences.getString(KEY_HOBBY_ID, null))
    }

    private companion object {
        const val PREFERENCES_NAME = "selected_hobby"
        const val KEY_HOBBY_ID = "hobby_id"
        const val KEY_HOBBY_IDS = "hobby_ids"
        const val SEPARATOR = ","
        const val DEFAULT_HOBBY_ID = "climbing"
    }
}
