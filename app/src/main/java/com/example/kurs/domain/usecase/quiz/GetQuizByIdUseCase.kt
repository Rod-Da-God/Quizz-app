package com.example.kurs.domain.usecase.quiz

import com.example.kurs.data.repository.QuizRepository
import com.example.kurs.data.db.relation.QuizWithBlocks
import javax.inject.Inject

class GetQuizByIdUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(quizId: Long): QuizWithBlocks =
        quizRepository.getQuizWithBlocks(quizId)
}