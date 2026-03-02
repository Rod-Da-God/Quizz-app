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
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizResultDao(): QuizResultDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}