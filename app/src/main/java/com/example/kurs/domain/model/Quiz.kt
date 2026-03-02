package com.example.kurs.domain.model

data class Quiz(
    val id: Long,
    val authorId: Long,
    val title: String,
    val description: String,
    val category: String,
    val blocks: List<Block> = emptyList()
)