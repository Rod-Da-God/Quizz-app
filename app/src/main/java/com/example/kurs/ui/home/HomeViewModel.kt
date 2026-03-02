package com.example.kurs.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kurs.data.db.entity.QuizEntity
import com.example.kurs.data.repository.UserRepository
import com.example.kurs.domain.usecase.quiz.DeleteQuizUseCase
import com.example.kurs.domain.usecase.quiz.GetAllQuizzesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllQuizzesUseCase: GetAllQuizzesUseCase,
    private val deleteQuizUseCase: DeleteQuizUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _quizzes = MutableStateFlow<List<QuizEntity>>(emptyList())
    val quizzes: StateFlow<List<QuizEntity>> = _quizzes

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserId().collect { _currentUserId.value = it }
        }
        viewModelScope.launch {
            getAllQuizzesUseCase().collect { _quizzes.value = it }
        }
    }

    fun deleteQuiz(quiz: QuizEntity) {
        viewModelScope.launch { deleteQuizUseCase(quiz) }
    }
}