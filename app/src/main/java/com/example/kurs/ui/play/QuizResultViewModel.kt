package com.example.kurs.ui.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kurs.data.db.entity.QuizResultEntity
import com.example.kurs.data.repository.QuizResultRepository
import com.example.kurs.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    private val quizResultRepository: QuizResultRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _result = MutableStateFlow<QuizResultEntity?>(null)
    val result: StateFlow<QuizResultEntity?> = _result

    fun loadResult(quizId: Long) {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId().first() ?: return@launch
            _result.value = quizResultRepository.getLastResult(userId, quizId)
        }
    }
}