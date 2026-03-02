package com.example.kurs.data.repository

import com.example.kurs.data.db.dao.UserDao
import com.example.kurs.data.db.entity.UserEntity
import com.example.kurs.data.preferences.SessionPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val prefs: SessionPreferences
) {
    suspend fun register(username: String, email: String, passwordHash: String): Long {
        val user = UserEntity(username = username, email = email, passwordHash = passwordHash)
        return userDao.insert(user)
    }

    suspend fun login(email: String, passwordHash: String): UserEntity? {
        val user = userDao.getByEmail(email)
        return if (user?.passwordHash == passwordHash) user else null
    }

    suspend fun emailExists(email: String): Boolean = userDao.emailExists(email)

    suspend fun getUserById(id: Long): UserEntity? = userDao.getById(id)

    suspend fun saveSession(userId: Long) = prefs.saveUserId(userId)

    suspend fun logout() = prefs.clear()

    fun getCurrentUserId(): Flow<Long?> = prefs.userId
}