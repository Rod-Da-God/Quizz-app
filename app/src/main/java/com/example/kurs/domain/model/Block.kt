package com.example.kurs.domain.model

import com.example.kurs.data.db.entity.BlockLevel

data class Block(
    val id: Long,
    val quizId: Long,
    val level: BlockLevel,
    val questions: List<Question> = emptyList()
)