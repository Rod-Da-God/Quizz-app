package com.example.kurs.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.kurs.data.db.dao.*
import com.example.kurs.data.db.entity.*

@Database(
    entities = [
        UserEntity::class,
        QuizEntity::class,
        BlockEntity::class,
        QuestionEntity::class,
        AnswerOptionEntity::class,
        QuizResultEntity::class,
        AchievementEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizResultDao(): QuizResultDao
    abstract fun achievementDao(): AchievementDao
}