package com.example.hobbymate.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hobbymate.data.local.HobbyMateDatabase
import com.example.hobbymate.data.local.dao.GuideDao
import com.example.hobbymate.data.local.dao.HobbyDao
import com.example.hobbymate.data.local.dao.PreferenceDao
import com.example.hobbymate.data.local.dao.ReviewDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val migration1To2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE daily_reviews ADD COLUMN title TEXT NOT NULL DEFAULT ''",
            )
            db.execSQL(
                "ALTER TABLE daily_reviews ADD COLUMN hardPart TEXT NOT NULL DEFAULT ''",
            )
            db.execSQL(
                "ALTER TABLE daily_reviews ADD COLUMN completedSteps INTEGER NOT NULL DEFAULT 0",
            )
            db.execSQL(
                "ALTER TABLE daily_reviews ADD COLUMN totalSteps INTEGER NOT NULL DEFAULT 0",
            )
            db.execSQL(
                "ALTER TABLE daily_reviews ADD COLUMN currentStage TEXT NOT NULL DEFAULT ''",
            )
        }
    }

    private val migration2To3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE daily_reviews ADD COLUMN hobbyId TEXT NOT NULL DEFAULT ''",
            )
            db.execSQL(
                "ALTER TABLE daily_reviews ADD COLUMN weekNumber INTEGER NOT NULL DEFAULT 1",
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HobbyMateDatabase =
        Room.databaseBuilder(
            context,
            HobbyMateDatabase::class.java,
            "hobby_mate.db",
        ).addMigrations(migration1To2, migration2To3).build()

    @Provides
    fun provideHobbyDao(database: HobbyMateDatabase): HobbyDao = database.hobbyDao()

    @Provides
    fun providePreferenceDao(database: HobbyMateDatabase): PreferenceDao =
        database.preferenceDao()

    @Provides
    fun provideGuideDao(database: HobbyMateDatabase): GuideDao = database.guideDao()

    @Provides
    fun provideReviewDao(database: HobbyMateDatabase): ReviewDao = database.reviewDao()
}
