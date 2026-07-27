package com.example.hobbymate

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hobbymate.data.local.HobbyMateDatabase
import com.example.hobbymate.data.local.entity.HobbyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HobbyMateDatabaseTest {

    private lateinit var database: HobbyMateDatabase

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HobbyMateDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun hobbyCanBeSavedAndRead() = runBlocking {
        val hobby = HobbyEntity(
            id = "drawing",
            name = "드로잉",
            category = "ART",
            description = "",
            estimatedCost = "",
            difficulty = 1,
            supplies = "",
            environmentScore = 0.1,
            socialScore = 0.1,
            physicalScore = 0.1,
            budgetScore = 0.5,
            creativityScore = 0.9,
        )

        database.hobbyDao().upsertAll(listOf(hobby))

        assertEquals(hobby, database.hobbyDao().observeAll().first().single())
    }
}
