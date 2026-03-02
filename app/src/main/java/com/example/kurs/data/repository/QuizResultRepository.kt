package com.example.kurs.data.repository

import com.example.kurs.data.db.dao.QuizResultDao
import com.example.kurs.data.db.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuizResultRepository @Inject constructor(
    private val quizResultDao: QuizResultDao
) {
    suspend fun saveResult(result: QuizResultEntity) = quizResultDao.insert(result)

    fun getResultsByUser(userId: Long): Flow<List<QuizResultEntity>> =
        quizResultDao.getResultsByUser(userId)

    suspend fun getCompletedCount(userId: Long): Int =
        quizResultDao.getCompletedQuizzesCount(userId)

    suspend fun getLastResult(userId: Long, quizId: Long): QuizResultEntity? =
        quizResultDao.getLastResult(userId, quizId)
}