package com.example.hobbymate.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hobbymate.data.local.entity.PreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferenceDao {

    @Query("SELECT * FROM preferences WHERE id = 1")
    fun observe(): Flow<PreferenceEntity?>

    @Upsert
    suspend fun upsert(preference: PreferenceEntity)
}
