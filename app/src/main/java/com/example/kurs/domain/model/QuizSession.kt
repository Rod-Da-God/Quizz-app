package com.example.kurs.domain.model

data class QuizSession(
    val quizId: Long,
    val userId: Long,
    val currentBlockIndex: Int = 0,
    val currentQuestionIndex: Int = 0,
    val answers: Map<Long, Long> = emptyMap(),
    val startTimeMillis: Long = System.currentTimeMillis()
)