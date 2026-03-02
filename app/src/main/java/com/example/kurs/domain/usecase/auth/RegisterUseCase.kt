package com.example.kurs.domain.usecase.auth

import com.example.kurs.data.repository.UserRepository
import com.example.kurs.domain.model.User
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): Result<User> {
        if (username.isBlank() || email.isBlank() || password.isBlank())
            return Result.failure(Exception("Заполните все поля"))

        if (password.length < 6)
            return Result.failure(Exception("Пароль минимум 6 символов"))

        if (userRepository.emailExists(email))
            return Result.failure(Exception("Email уже занят"))

        val passwordHash = password.hashCode().toString()
        val id = userRepository.register(username, email, passwordHash)
        userRepository.saveSession(id)
        return Result.success(User(id, username, email))
    }
}