package com.example.kurs.data.db.dao

import androidx.room.*
import com.example.kurs.data.db.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: QuizResultEntity)

    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY completedAt DESC")
    fun getResultsByUser(userId: Long): Flow<List<QuizResultEntity>>

    @Query("SELECT COUNT(*) FROM quiz_results WHERE userId = :userId")
    suspend fun getCompletedQuizzesCount(userId: Long): Int

    @Query("SELECT * FROM quiz_results WHERE userId = :userId AND quizId = :quizId ORDER BY completedAt DESC LIMIT 1")
    suspend fun getLastResult(userId: Long, quizId: Long): QuizResultEntity?
}