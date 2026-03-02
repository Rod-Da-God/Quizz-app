package com.example.kurs.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class BlockLevel  {
    EASY, MEDIUM, HARD
}

@Entity(
    tableName = "blocks",
    foreignKeys = [ForeignKey(
        entity = QuizEntity::class,
        parentColumns = ["id"],
        childColumns = ["quizId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("quizId")]

)
data class BlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long =0,
    val quizId: Long,
    val level: BlockLevel,
    val orderIndex: Int
)