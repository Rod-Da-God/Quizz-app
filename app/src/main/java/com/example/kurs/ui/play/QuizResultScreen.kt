package com.example.kurs.ui.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kurs.data.db.entity.BlockLevel
import com.example.kurs.data.db.relation.BlockWithQuestions
import com.example.kurs.data.db.relation.QuizWithBlocks
import com.example.kurs.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayScreen(
    navController: NavController,
    quizId: Long,
    viewModel: QuizPlayViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(quizId) { viewModel.loadQuiz(quizId) }

    LaunchedEffect(state) {
        if (state is PlayState.Finished) {
            navController.navigate(Screen.Result.createRoute((state as PlayState.Finished).quizId)) {
                popUpTo(Screen.Play.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Тест") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is PlayState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PlayState.BlockIntro -> {
                    BlockIntroContent(
                        level = s.level,
                        questionCount = s.questionCount,
                        onStart = { viewModel.startBlock() }
                    )
                }
                is PlayState.Question -> {
                    QuestionContent(
                        quiz = s.quiz,
                        block = s.block,
                        questionIndex = s.questionIndex,
                        selectedOptionId = s.selectedOptionId,
                        onSelectAnswer = { viewModel.selectAnswer(it) },
                        onNext = { viewModel.nextQuestion() }
                    )
                }
                is PlayState.Error -> {
                    Text(
                        text = s.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
fun BlockIntroContent(
    level: BlockLevel,
    questionCount: Int,
    onStart: () -> Unit
) {
    val levelName = when (level) {
        BlockLevel.EASY -> "Лёгкий"
        BlockLevel.MEDIUM -> "Средний"
        BlockLevel.HARD -> "Сложный"
    }
    val levelColor = when (level) {
        BlockLevel.EASY -> MaterialTheme.colorScheme.tertiary
        BlockLevel.MEDIUM -> MaterialTheme.colorScheme.secondary
        BlockLevel.HARD -> MaterialTheme.colorScheme.error
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Блок: $levelName",
            style = MaterialTheme.typography.headlineMedium,
            color = levelColor
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Вопросов: $questionCount",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Начать блок")
        }
    }
}

@Composable
fun QuestionContent(
    quiz: QuizWithBlocks,
    block: BlockWithQuestions,
    questionIndex: Int,
    selectedOptionId: Long?,
    onSelectAnswer: (Long) -> Unit,
    onNext: () -> Unit
) {
    val question = block.questions[questionIndex]
    val totalQuestions = block.questions.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LinearProgressIndicator(
                progress = { (questionIndex + 1).toFloat() / totalQuestions },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Вопрос ${questionIndex + 1} из $totalQuestions",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = question.question.text,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        items(question.options.size) { index ->
            val option = question.options[index]
            val isSelected = option.id == selectedOptionId
            Card(
                onClick = { onSelectAnswer(option.id) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) ButtonDefaults.outlinedButtonBorder else null
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isSelected, onClick = { onSelectAnswer(option.id) })
                    Spacer(Modifier.width(8.dp))
                    Text(text = option.text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedOptionId != null
            ) {
                val isLastQuestion = questionIndex == totalQuestions - 1
                val isLastBlock = quiz.blocks.lastOrNull()?.block?.id == block.block.id
                Text(
                    when {
                        !isLastQuestion -> "Следующий вопрос"
                        !isLastBlock -> "Следующий блок"
                        else -> "Завершить тест"
                    }
                )
            }
        }
    }
}