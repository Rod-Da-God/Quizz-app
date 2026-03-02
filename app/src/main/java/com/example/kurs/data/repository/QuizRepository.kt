package com.example.kurs.data.repository

import com.example.kurs.data.db.dao.QuestionDao
import com.example.kurs.data.db.dao.QuizDao
import com.example.kurs.data.db.entity.AnswerOptionEntity
import com.example.kurs.data.db.entity.BlockEntity
import com.example.kurs.data.db.entity.QuestionEntity
import com.example.kurs.data.db.entity.QuizEntity
import com.example.kurs.data.db.relation.QuizWithBlocks
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val quizDao: QuizDao,
    private val questionDao: QuestionDao
) {
    suspend fun createQuiz(quiz: QuizEntity): Long = quizDao.insert(quiz)

    suspend fun deleteQuiz(quiz: QuizEntity) = quizDao.delete(quiz)

    fun getAllQuizzes(): Flow<List<QuizEntity>> = quizDao.getAllQuizzes()

    fun getQuizzesByUser(userId: Long): Flow<List<QuizEntity>> =
        quizDao.getQuizzesByUser(userId)

    suspend fun getQuizWithBlocks(quizId: Long): QuizWithBlocks =
        quizDao.getQuizWithBlocks(quizId)


    suspend fun insertBlock(block: BlockEntity): Long = questionDao.insertBlock(block)

    suspend fun insertQuestion(question: QuestionEntity): Long = questionDao.insertQuestion(question)

    suspend fun insertOptions(options: List<AnswerOptionEntity>) = questionDao.insertOptions(options)
}