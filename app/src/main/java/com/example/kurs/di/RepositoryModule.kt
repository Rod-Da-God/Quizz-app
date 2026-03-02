package com.example.kurs.di

import com.example.kurs.data.db.dao.*
import com.example.kurs.data.preferences.SessionPreferences
import com.example.kurs.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        prefs: SessionPreferences
    ): UserRepository = UserRepository(userDao, prefs)

    @Provides @Singleton
    fun provideQuizRepository(
        quizDao: QuizDao,
        questionDao: QuestionDao
    ): QuizRepository = QuizRepository(quizDao, questionDao)

    @Provides @Singleton
    fun provideQuizResultRepository(
        quizResultDao: QuizResultDao
    ): QuizResultRepository = QuizResultRepository(quizResultDao)

    @Provides @Singleton
    fun provideAchievementRepository(
        achievementDao: AchievementDao
    ): AchievementRepository = AchievementRepository(achievementDao)
}