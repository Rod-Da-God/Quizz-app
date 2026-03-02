package com.example.kurs.data.db.dao

import androidx.room.*
import com.example.kurs.data.db.entity.QuizEntity
import com.example.kurs.data.db.relation.QuizWithBlocks
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quiz: QuizEntity): Long

    @Delete
    suspend fun delete(quiz: QuizEntity)

    @Query("SELECT * FROM quizzes ORDER BY createdAt DESC")
    fun getAllQuizzes(): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE authorId = :userId ORDER BY createdAt DESC")
    fun getQuizzesByUser(userId: Long): Flow<List<QuizEntity>>

    @Transaction
    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizWithBlocks(quizId: Long): QuizWithBlocks
}