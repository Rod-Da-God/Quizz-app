package com.example.kurs.domain.usecase.quiz

import com.example.kurs.data.db.entity.QuizEntity
import com.example.kurs.data.repository.QuizRepository
import javax.inject.Inject

class DeleteQuizUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(quiz: QuizEntity) = quizRepository.deleteQuiz(quiz)
}