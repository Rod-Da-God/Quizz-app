package com.example.kurs.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kurs.data.db.relation.QuestionWithOptions
import com.example.kurs.data.db.relation.QuizWithBlocks
import com.example.kurs.domain.usecase.quiz.GetQuizByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudyState(
    val quiz: QuizWithBlocks? = null,
    val allQuestions: List<QuestionWithOptions> = emptyList(),
    val currentIndex: Int = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val currentQuestion: QuestionWithOptions?
        get() = allQuestions.getOrNull(currentIndex)
    val progress: Float
        get() = if (allQuestions.isEmpty()) 0f
        else (currentIndex + 1).toFloat() / allQuestions.size
}

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val getQuizByIdUseCase: GetQuizByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StudyState())
    val state: StateFlow<StudyState> = _state

    fun loadQuiz(quizId: Long) {
        viewModelScope.launch {
            try {
                val quiz = getQuizByIdUseCase(quizId)
                val questions = quiz.blocks
                    .sortedBy { it.block.orderIndex }
                    .flatMap { block ->
                        block.questions.sortedBy { it.question.orderIndex }
                    }
                _state.value = StudyState(
                    quiz = quiz,
                    allQuestions = questions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = StudyState(isLoading = false, error = e.message)
            }
        }
    }

    fun next() {
        val s = _state.value
        if (s.currentIndex < s.allQuestions.lastIndex) {
            _state.value = s.copy(currentIndex = s.currentIndex + 1)
        } else {
            _state.value = s.copy(isFinished = true)
        }
    }

    fun previous() {
        val s = _state.value
        if (s.currentIndex > 0) {
            _state.value = s.copy(currentIndex = s.currentIndex - 1)
        }
    }
}