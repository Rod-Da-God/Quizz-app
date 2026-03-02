package com.example.kurs.data.db.dao

import androidx.room.*
import com.example.kurs.data.db.entity.AchievementEntity
import com.example.kurs.data.db.entity.AchievementType
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements WHERE userId = :userId")
    fun getByUser(userId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM achievements WHERE userId = :userId AND type = :type)")
    suspend fun isUnlocked(userId: Long, type: AchievementType): Boolean
}