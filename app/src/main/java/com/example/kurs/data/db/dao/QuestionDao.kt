package com.example.kurs.data.db.dao

import androidx.room.*
import com.example.kurs.data.db.entity.AnswerOptionEntity
import com.example.kurs.data.db.entity.BlockEntity
import com.example.kurs.data.db.entity.QuestionEntity

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<AnswerOptionEntity>)

    @Transaction
    suspend fun insertBlockWithQuestions(
        block: BlockEntity,
        questions: List<Pair<QuestionEntity, List<AnswerOptionEntity>>>
    ) {
        val blockId = insertBlock(block)
        questions.forEach { (question, options) ->
            val questionId = insertQuestion(question.copy(blockId = blockId))
            insertOptions(options.map { it.copy(questionId = questionId) })
        }
    }
}