package com.example.kurs.domain.usecase.auth

import com.example.kurs.data.repository.UserRepository
import com.example.kurs.domain.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank())
            return Result.failure(Exception("Заполните все поля"))

        val passwordHash = password.hashCode().toString()
        val entity = userRepository.login(email, passwordHash)
            ?: return Result.failure(Exception("Неверный email или пароль"))

        userRepository.saveSession(entity.id)
        return Result.success(User(entity.id, entity.username, entity.email))
    }
}