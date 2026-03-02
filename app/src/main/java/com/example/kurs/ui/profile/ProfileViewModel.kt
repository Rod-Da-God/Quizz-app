package com.example.kurs.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kurs.data.db.entity.AchievementEntity
import com.example.kurs.data.db.entity.QuizResultEntity
import com.example.kurs.data.db.entity.UserEntity
import com.example.kurs.data.repository.AchievementRepository
import com.example.kurs.data.repository.QuizResultRepository
import com.example.kurs.data.repository.UserRepository
import com.example.kurs.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserEntity? = null,
    val results: List<QuizResultEntity> = emptyList(),
    val achievements: List<AchievementEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val quizResultRepository: QuizResultRepository,
    private val achievementRepository: AchievementRepository,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserId().collectLatest { userId ->
                if (userId == null) return@collectLatest

                val user = userRepository.getUserById(userId)
                _state.update { it.copy(user = user) }

                combine(
                    quizResultRepository.getResultsByUser(userId),
                    achievementRepository.getByUser(userId)
                ) { results, achievements ->
                    results to achievements
                }.collect { (results, achievements) ->
                    _state.update {
                        it.copy(results = results, achievements = achievements, isLoading = false)
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _loggedOut.value = true
        }
    }
}