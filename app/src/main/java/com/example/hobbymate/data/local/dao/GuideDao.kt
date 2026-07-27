package com.example.hobbymate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hobbymate.data.local.entity.GuideEntity
import com.example.hobbymate.data.local.entity.SessionEntity
import com.example.hobbymate.data.local.entity.WeekEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideDao {

    @Query("SELECT * FROM guides ORDER BY createdAt DESC")
    fun observeGuides(): Flow<List<GuideEntity>>

    @Query("SELECT * FROM weeks WHERE guideId = :guideId ORDER BY weekNumber")
    fun observeWeeks(guideId: Long): Flow<List<WeekEntity>>

    @Query("SELECT * FROM sessions WHERE weekId = :weekId ORDER BY dayNumber")
    fun observeSessions(weekId: Long): Flow<List<SessionEntity>>

    @Insert
    suspend fun insertGuide(guide: GuideEntity): Long

    @Insert
    suspend fun insertWeeks(weeks: List<WeekEntity>): List<Long>

    @Insert
    suspend fun insertSessions(sessions: List<SessionEntity>)

    @Query("UPDATE sessions SET isCompleted = 1 WHERE id = :sessionId")
    suspend fun completeSession(sessionId: Long)

    @Update
    suspend fun updateSession(session: SessionEntity)
}
