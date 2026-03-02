package com.example.kurs.domain.model

data class AnswerOption(
    val id: Long,
    val questionId: Long,
    val text: String,
    val isCorrect: Boolean
)