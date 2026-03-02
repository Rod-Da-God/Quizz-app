package com.example.kurs.ui.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kurs.data.db.entity.BlockLevel
import com.example.kurs.data.db.entity.QuestionType
import com.example.kurs.data.db.relation.BlockWithQuestions
import com.example.kurs.data.db.relation.QuizWithBlocks
import com.example.kurs.data.repository.UserRepository
import com.example.kurs.domain.usecase.achievement.CheckAchievementsUseCase
import com.example.kurs.domain.usecase.quiz.GetQuizByIdUseCase
import com.example.kurs.domain.usecase.result.CalculateResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PlayState {
    object Loading : PlayState()
    data class BlockIntro(val level: BlockLevel, val questionCount: Int) : PlayState()
    data class Question(
        val quiz: QuizWithBlocks,
        val block: BlockWithQuestions,
        val questionIndex: Int,
        val selectedOptionIds: Set<Long>
    ) : PlayState()
    data class Finished(val quizId: Long) : PlayState()
    data class Error(val message: String) : PlayState()
}

@HiltViewModel
class QuizPlayViewModel @Inject constructor(
    private val getQuizByIdUseCase: GetQuizByIdUseCase,
    private val calculateResultUseCase: CalculateResultUseCase,
    private val checkAchievementsUseCase: CheckAchievementsUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PlayState>(PlayState.Loading)
    val state: StateFlow<PlayState> = _state

    private var quiz: QuizWithBlocks? = null
    private var currentBlockIndex = 0
    private var currentQuestionIndex = 0
    private val answers = mutableMapOf<Long, Set<Long>>() // questionId -> selected optionIds
    private var startTime = System.currentTimeMillis()

    fun loadQuiz(quizId: Long) {
        viewModelScope.launch {
            try {
                quiz = getQuizByIdUseCase(quizId)
                showBlockIntro()
            } catch (e: Exception) {
                _state.value = PlayState.Error(e.message ?: "Ошибка загрузки теста")
            }
        }
    }

    private fun showBlockIntro() {
        val q = quiz ?: return
        val block = q.blocks.getOrNull(currentBlockIndex) ?: return
        _state.value = PlayState.BlockIntro(
            level = block.block.level,
            questionCount = block.questions.size
        )
    }

    fun startBlock() {
        currentQuestionIndex = 0
        showQuestion()
    }

    fun selectAnswer(optionId: Long) {
        val current = _state.value as? PlayState.Question ?: return
        val currentQuestion = current.block.questions[current.questionIndex]
        val questionId = currentQuestion.question.id

        val updatedSelection = if (currentQuestion.question.type == QuestionType.MULTIPLY_CHOICE) {
            if (current.selectedOptionIds.contains(optionId)) {
                current.selectedOptionIds - optionId
            } else {
                current.selectedOptionIds + optionId
            }
        } else {
            setOf(optionId)
        }

        answers[questionId] = updatedSelection
        _state.value = current.copy(selectedOptionIds = updatedSelection)
    }

    fun nextQuestion() {
        val q = quiz ?: return
        val block = q.blocks.getOrNull(currentBlockIndex) ?: return

        if (currentQuestionIndex < block.questions.lastIndex) {
            currentQuestionIndex++
            showQuestion()
        } else {
            // Блок закончен
            if (currentBlockIndex < q.blocks.lastIndex) {
                currentBlockIndex++
                showBlockIntro()
            } else {
                finishQuiz()
            }
        }
    }

    private fun showQuestion() {
        val q = quiz ?: return
        val block = q.blocks.getOrNull(currentBlockIndex) ?: return
        val questionId = block.questions.getOrNull(currentQuestionIndex)?.question?.id
        _state.value = PlayState.Question(
            quiz = q,
            block = block,
            questionIndex = currentQuestionIndex,
            selectedOptionIds = questionId?.let { answers[it] } ?: emptySet()
        )
    }

    private fun finishQuiz() {
        val q = quiz ?: return
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId().first() ?: return@launch
            val timeSpent = (System.currentTimeMillis() - startTime) / 1000

            val result = calculateResultUseCase(userId, q, answers, timeSpent)
            checkAchievementsUseCase(userId, result)

            _state.value = PlayState.Finished(q.quiz.id)
        }
    }
}
