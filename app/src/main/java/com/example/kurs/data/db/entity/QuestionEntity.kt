package com.example.kurs.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class QuestionType  {
    SINGLE_CHOICE, MULTIPLY_CHOICE, TRUE_FALSE
}

@Entity(
    tableName = "questions",
    foreignKeys = [ForeignKey(
        entity = QuizEntity::class,
        parentColumns = ["id"],
        childColumns = ["blockId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("blockId")]

)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long =0,
    val blockId: Long,
    val text: String,
    val imageUri: String? = null,
    val type: QuestionType,
    val orderIndex: Int
)