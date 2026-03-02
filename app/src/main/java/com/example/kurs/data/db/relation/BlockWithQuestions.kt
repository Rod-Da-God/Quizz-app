package com.example.kurs.data.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.kurs.data.db.entity.AnswerOptionEntity
import com.example.kurs.data.db.entity.BlockEntity
import com.example.kurs.data.db.entity.QuestionEntity

data class QuestionWithOptions(
    @Embedded val question: QuestionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "questionId"
    )
    val options: List<AnswerOptionEntity>
)

data class BlockWithQuestions(
    @Embedded val block: BlockEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "blockId",
        entity = QuestionEntity::class
    )
    val questions: List<QuestionWithOptions>
)