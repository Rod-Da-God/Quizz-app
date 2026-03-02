package com.example.kurs.domain.usecase.achievement

import com.example.kurs.data.db.entity.AchievementType
import com.example.kurs.data.repository.AchievementRepository
import com.example.kurs.data.repository.QuizResultRepository
import com.example.kurs.domain.model.QuizResult
import javax.inject.Inject

class CheckAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val quizResultRepository: QuizResultRepository
) {
    suspend operator fun invoke(userId: Long, result: QuizResult) {
        val completedCount = quizResultRepository.getCompletedCount(userId)
        if (completedCount == 1)
            achievementRepository.unlock(userId, AchievementType.FIRST_QUIZ)

        if (result.overallPercent == 100f)
            achievementRepository.unlock(userId, AchievementType.PERFECT_SCORE)

        if (result.overallPercent >= 50f)
            achievementRepository.unlock(userId, AchievementType.HALF_WAY)

        if (result.hardResult.total > 0 && result.hardResult.percent == 100f)
            achievementRepository.unlock(userId, AchievementType.HARD_BLOCK_DONE)

        if (completedCount >= 10)
            achievementRepository.unlock(userId, AchievementType.DEDICATED)

        val totalQuestions = result.easyResult.total + result.mediumResult.total + result.hardResult.total
        if (totalQuestions > 0 && result.timeSpentSeconds / totalQuestions < 10)
            achievementRepository.unlock(userId, AchievementType.SPEED_RUNNER)
    }
}