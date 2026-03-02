package com.example.kurs.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kurs.data.db.entity.*
import com.example.kurs.data.repository.QuizRepository
import com.example.kurs.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionDraft(
    val id: Int = System.currentTimeMillis().toInt(),
    val text: String = "",
    val imageUri: String? = null,
    val type: QuestionType = QuestionType.SINGLE_CHOICE,
    val options: List<OptionDraft> = listOf(
        OptionDraft(text = "", isCorrect = false),
        OptionDraft(text = "", isCorrect = false),
        OptionDraft(text = "", isCorrect = false),
        OptionDraft(text = "", isCorrect = false)
    )
)

data class OptionDraft(
    val text: String = "",
    val isCorrect: Boolean = false
)

data class CreateQuizUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val easyQuestions: List<QuestionDraft> = listOf(QuestionDraft()),
    val mediumQuestions: List<QuestionDraft> = listOf(QuestionDraft()),
    val hardQuestions: List<QuestionDraft> = listOf(QuestionDraft()),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreateQuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateQuizUiState())
    val uiState: StateFlow<CreateQuizUiState> = _uiState

    fun updateTitle(value: String) { _uiState.value = _uiState.value.copy(title = value) }
    fun updateDescription(value: String) { _uiState.value = _uiState.value.copy(description = value) }
    fun updateCategory(value: String) { _uiState.value = _uiState.value.copy(category = value) }

    fun updateQuestion(level: BlockLevel, index: Int, draft: QuestionDraft) {
        _uiState.value = when (level) {
            BlockLevel.EASY -> _uiState.value.copy(
                easyQuestions = _uiState.value.easyQuestions.toMutableList().also { it[index] = draft }
            )
            BlockLevel.MEDIUM -> _uiState.value.copy(
                mediumQuestions = _uiState.value.mediumQuestions.toMutableList().also { it[index] = draft }
            )
            BlockLevel.HARD -> _uiState.value.copy(
                hardQuestions = _uiState.value.hardQuestions.toMutableList().also { it[index] = draft }
            )
        }
    }

    fun addQuestion(level: BlockLevel) {
        _uiState.value = when (level) {
            BlockLevel.EASY -> _uiState.value.copy(
                easyQuestions = _uiState.value.easyQuestions + QuestionDraft()
            )
            BlockLevel.MEDIUM -> _uiState.value.copy(
                mediumQuestions = _uiState.value.mediumQuestions + QuestionDraft()
            )
            BlockLevel.HARD -> _uiState.value.copy(
                hardQuestions = _uiState.value.hardQuestions + QuestionDraft()
            )
        }
    }

    fun removeQuestion(level: BlockLevel, index: Int) {
        _uiState.value = when (level) {
            BlockLevel.EASY -> {
                val list = _uiState.value.easyQuestions.toMutableList().also { it.removeAt(index) }
                _uiState.value.copy(easyQuestions = list.ifEmpty { listOf(QuestionDraft()) })
            }
            BlockLevel.MEDIUM -> {
                val list = _uiState.value.mediumQuestions.toMutableList().also { it.removeAt(index) }
                _uiState.value.copy(mediumQuestions = list.ifEmpty { listOf(QuestionDraft()) })
            }
            BlockLevel.HARD -> {
                val list = _uiState.value.hardQuestions.toMutableList().also { it.removeAt(index) }
                _uiState.value.copy(hardQuestions = list.ifEmpty { listOf(QuestionDraft()) })
            }
        }
    }

    fun saveQuiz() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "Введите название теста")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            try {
                val userId = userRepository.getCurrentUserId().first() ?: return@launch

                val quizId = quizRepository.createQuiz(
                    QuizEntity(
                        authorId = userId,
                        title = state.title,
                        description = state.description,
                        category = state.category
                    )
                )

                listOf(
                    BlockLevel.EASY to state.easyQuestions,
                    BlockLevel.MEDIUM to state.mediumQuestions,
                    BlockLevel.HARD to state.hardQuestions
                ).forEachIndexed { index, (level, questions) ->
                    val blockId = quizRepository.insertBlock(
                        BlockEntity(quizId = quizId, level = level, orderIndex = index)
                    )
                    questions.forEachIndexed { qIndex, draft ->
                        val questionId = quizRepository.insertQuestion(
                            QuestionEntity(
                                blockId = blockId,
                                text = draft.text,
                                imageUri = draft.imageUri,
                                type = draft.type,
                                orderIndex = qIndex
                            )
                        )
                        quizRepository.insertOptions(
                            draft.options.mapIndexed { oIndex, option ->
                                AnswerOptionEntity(
                                    questionId = questionId,
                                    text = option.text,
                                    isCorrect = option.isCorrect,
                                    orderIndex = oIndex
                                )
                            }
                        )
                    }
                }

                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}