package com.example.kurs.domain.usecase.result

import com.example.kurs.data.db.entity.BlockLevel
import com.example.kurs.data.db.entity.QuizResultEntity
import com.example.kurs.data.db.relation.QuizWithBlocks
import com.example.kurs.data.repository.QuizResultRepository
import com.example.kurs.domain.model.BlockResult
import com.example.kurs.domain.model.QuizResult
import javax.inject.Inject

class CalculateResultUseCase @Inject constructor(
    private val quizResultRepository: QuizResultRepository
) {
    suspend operator fun invoke(
        userId: Long,
        quiz: QuizWithBlocks,
        answers: Map<Long, Long>,
        timeSpentSeconds: Long
    ): QuizResult {
        fun calcBlock(level: BlockLevel): BlockResult {
            val block = quiz.blocks.find { it.block.level == level }
                ?: return BlockResult(0, 0)
            var correct = 0
            block.questions.forEach { q ->
                val selected = answers[q.question.id]
                val isCorrect = q.options.any { it.id == selected && it.isCorrect }
                if (isCorrect) correct++
            }
            return BlockResult(correct, block.questions.size)
        }

        val easy = calcBlock(BlockLevel.EASY)
        val medium = calcBlock(BlockLevel.MEDIUM)
        val hard = calcBlock(BlockLevel.HARD)
        val totalCorrect = easy.correct + medium.correct + hard.correct
        val totalQuestions = easy.total + medium.total + hard.total
        val overall = if (totalQuestions > 0) totalCorrect.toFloat() / totalQuestions * 100 else 0f

        quizResultRepository.saveResult(
            QuizResultEntity(
                userId = userId,
                quizId = quiz.quiz.id,
                easyCorrect = easy.correct, easyTotal = easy.total,
                mediumCorrect = medium.correct, mediumTotal = medium.total,
                hardCorrect = hard.correct, hardTotal = hard.total,
                overallPercent = overall,
                timeSpentSeconds = timeSpentSeconds
            )
        )

        return QuizResult(quiz.quiz.id, userId, easy, medium, hard, overall, timeSpentSeconds)
    }
}