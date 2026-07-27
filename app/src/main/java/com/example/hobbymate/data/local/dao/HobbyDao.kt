package com.example.hobbymate.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hobbymate.data.local.entity.HobbyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HobbyDao {

    @Query("SELECT * FROM hobbies ORDER BY name")
    fun observeAll(): Flow<List<HobbyEntity>>

    @Query("SELECT * FROM hobbies WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): HobbyEntity?

    @Upsert
    suspend fun upsertAll(hobbies: List<HobbyEntity>)
}
