package com.example.kurs.domain.model

import com.example.kurs.data.db.entity.QuestionType

data class Question(
    val id: Long,
    val blockId: Long,
    val text: String,
    val imageUri: String? = null,
    val type: QuestionType,
    val options: List<AnswerOption> = emptyList()
)