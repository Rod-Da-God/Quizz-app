package com.example.kurs.domain.usecase.quiz

import com.example.kurs.data.db.entity.QuizEntity
import com.example.kurs.data.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllQuizzesUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    operator fun invoke(): Flow<List<QuizEntity>> = quizRepository.getAllQuizzes()
}