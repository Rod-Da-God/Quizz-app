package com.example.kurs.data.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.kurs.data.db.entity.BlockEntity
import com.example.kurs.data.db.entity.QuizEntity

data class QuizWithBlocks(
    @Embedded val quiz: QuizEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "quizId",
        entity = BlockEntity::class
    )
    val blocks: List<BlockWithQuestions>
)