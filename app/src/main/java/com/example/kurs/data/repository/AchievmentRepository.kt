package com.example.kurs.data.repository

import com.example.kurs.data.db.dao.AchievementDao
import com.example.kurs.data.db.entity.AchievementEntity
import com.example.kurs.data.db.entity.AchievementType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AchievementRepository @Inject constructor(
    private val achievementDao: AchievementDao
) {
    suspend fun unlock(userId: Long, type: AchievementType) {
        if (!achievementDao.isUnlocked(userId, type)) {
            achievementDao.insert(AchievementEntity(userId = userId, type = type))
        }
    }

    fun getByUser(userId: Long): Flow<List<AchievementEntity>> =
        achievementDao.getByUser(userId)

    suspend fun isUnlocked(userId: Long, type: AchievementType): Boolean =
        achievementDao.isUnlocked(userId, type)
}