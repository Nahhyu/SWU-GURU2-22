package com.example.hobbymate.data.repository

import com.example.hobbymate.data.local.dao.HobbyDao
import com.example.hobbymate.data.local.entity.HobbyEntity
import com.example.hobbymate.model.Hobby
import com.example.hobbymate.model.HobbyCategory
import com.example.hobbymate.model.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HobbyRepository @Inject constructor(
    private val hobbyDao: HobbyDao,
) {
    fun observeHobbies(): Flow<List<Hobby>> =
        hobbyDao.observeAll().map { entities -> entities.map { it.toModel() } }

    suspend fun findHobby(id: String): Hobby? = hobbyDao.findById(id)?.toModel()

    suspend fun saveHobbies(hobbies: List<Hobby>) {
        hobbyDao.upsertAll(hobbies.map { it.toEntity() })
    }

    private fun HobbyEntity.toModel() = Hobby(
        id = id,
        name = name,
        category = runCatching { HobbyCategory.valueOf(category) }.getOrDefault(HobbyCategory.ETC),
        description = description,
        estimatedCost = estimatedCost,
        difficulty = difficulty,
        supplies = supplies.split("|").filter(String::isNotBlank),
        preferenceVector = UserPreference(
            environmentScore,
            socialScore,
            physicalScore,
            budgetScore,
            creativityScore,
        ),
    )

    private fun Hobby.toEntity() = HobbyEntity(
        id = id,
        name = name,
        category = category.name,
        description = description,
        estimatedCost = estimatedCost,
        difficulty = difficulty,
        supplies = supplies.joinToString("|"),
        environmentScore = preferenceVector.environment,
        socialScore = preferenceVector.social,
        physicalScore = preferenceVector.physicalIntensity,
        budgetScore = preferenceVector.budget,
        creativityScore = preferenceVector.structureCreativity,
    )
}
