package com.example.kurs.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_results",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuizEntity::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("quizId")]
)
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val quizId: Long,
    val easyCorrect: Int,
    val easyTotal: Int,
    val mediumCorrect: Int,
    val mediumTotal: Int,
    val hardCorrect: Int,
    val hardTotal: Int,
    val overallPercent: Float,
    val timeSpentSeconds: Long,
    val completedAt: Long = System.currentTimeMillis()
)