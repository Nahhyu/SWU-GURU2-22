package com.example.hobbymate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hobbymate.data.local.dao.GuideDao
import com.example.hobbymate.data.local.dao.HobbyDao
import com.example.hobbymate.data.local.dao.PreferenceDao
import com.example.hobbymate.data.local.dao.ReviewDao
import com.example.hobbymate.data.local.entity.DailyReviewEntity
import com.example.hobbymate.data.local.entity.GuideEntity
import com.example.hobbymate.data.local.entity.HobbyEntity
import com.example.hobbymate.data.local.entity.PreferenceEntity
import com.example.hobbymate.data.local.entity.SessionEntity
import com.example.hobbymate.data.local.entity.VideoEntity
import com.example.hobbymate.data.local.entity.WeekEntity
import com.example.hobbymate.data.local.entity.WeeklyReviewEntity

@Database(
    entities = [
        HobbyEntity::class,
        PreferenceEntity::class,
        GuideEntity::class,
        WeekEntity::class,
        SessionEntity::class,
        VideoEntity::class,
        DailyReviewEntity::class,
        WeeklyReviewEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class HobbyMateDatabase : RoomDatabase() {
    abstract fun hobbyDao(): HobbyDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun guideDao(): GuideDao
    abstract fun reviewDao(): ReviewDao
}
