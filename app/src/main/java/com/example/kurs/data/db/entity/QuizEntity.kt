package com.example.kurs.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quizzes",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["authorId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("authorId")]

)
data class QuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Long =0,
    val authorId: Long,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)