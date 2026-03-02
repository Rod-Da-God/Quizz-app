package com.example.kurs.domain.model

data class BlockResult(
    val correct: Int,
    val total: Int,
    val percent: Float = if (total > 0) correct.toFloat() / total * 100 else 0f
)

data class QuizResult(
    val quizId: Long,
    val userId: Long,
    val easyResult: BlockResult,
    val mediumResult: BlockResult,
    val hardResult: BlockResult,
    val overallPercent: Float,
    val timeSpentSeconds: Long
)