package com.example.kurs.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AchievementType {
    FIRST_QUIZ,
    PERFECT_SCORE,
    HARD_BLOCK_DONE,
    SPEED_RUNNER,
    COLLECTOR,
    HALF_WAY,
    DEDICATED
}

@Entity(
    tableName = "achievements",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val type: AchievementType,
    val unlockedAt: Long = System.currentTimeMillis()
)