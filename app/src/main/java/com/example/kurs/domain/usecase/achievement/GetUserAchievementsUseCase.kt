package com.example.kurs.domain.usecase.achievement

import com.example.kurs.data.db.entity.AchievementEntity
import com.example.kurs.data.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository
) {
    operator fun invoke(userId: Long): Flow<List<AchievementEntity>> =
        achievementRepository.getByUser(userId)
}