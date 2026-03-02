package com.example.kurs.ui.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kurs.data.db.entity.BlockLevel
import com.example.kurs.data.db.entity.QuestionType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizScreen(
    navController: NavController,
    viewModel: CreateQuizViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создать тест") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveQuiz() },
                        enabled = !state.isSaving
                    ) {
                        Text("Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Основное", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = state.title,
                            onValueChange = { viewModel.updateTitle(it) },
                            label = { Text("Название теста") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.category,
                            onValueChange = { viewModel.updateCategory(it) },
                            label = { Text("Категория") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.description,
                            onValueChange = { viewModel.updateDescription(it) },
                            label = { Text("Описание") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }

            item {
                BlockSection(
                    title = "Лёгкий уровень",
                    questions = state.easyQuestions,
                    level = BlockLevel.EASY,
                    onQuestionChange = { index, draft -> viewModel.updateQuestion(BlockLevel.EASY, index, draft) },
                    onAddQuestion = { viewModel.addQuestion(BlockLevel.EASY) },
                    onRemoveQuestion = { index -> viewModel.removeQuestion(BlockLevel.EASY, index) }
                )
            }

            item {
                BlockSection(
                    title = "Средний уровень",
                    questions = state.mediumQuestions,
                    level = BlockLevel.MEDIUM,
                    onQuestionChange = { index, draft -> viewModel.updateQuestion(BlockLevel.MEDIUM, index, draft) },
                    onAddQuestion = { viewModel.addQuestion(BlockLevel.MEDIUM) },
                    onRemoveQuestion = { index -> viewModel.removeQuestion(BlockLevel.MEDIUM, index) }
                )
            }

            item {
                BlockSection(
                    title = "Сложный уровень",
                    questions = state.hardQuestions,
                    level = BlockLevel.HARD,
                    onQuestionChange = { index, draft -> viewModel.updateQuestion(BlockLevel.HARD, index, draft) },
                    onAddQuestion = { viewModel.addQuestion(BlockLevel.HARD) },
                    onRemoveQuestion = { index -> viewModel.removeQuestion(BlockLevel.HARD, index) }
                )
            }

            if (state.error != null) {
                item {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun BlockSection(
    title: String,
    questions: List<QuestionDraft>,
    level: BlockLevel,
    onQuestionChange: (Int, QuestionDraft) -> Unit,
    onAddQuestion: () -> Unit,
    onRemoveQuestion: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            questions.forEachIndexed { index, draft ->
                QuestionDraftItem(
                    index = index,
                    draft = draft,
                    onDraftChange = { onQuestionChange(index, it) },
                    onRemove = if (questions.size > 1) ({ onRemoveQuestion(index) }) else null
                )
                if (index < questions.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onAddQuestion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Добавить вопрос")
            }
        }
    }
}

@Composable
fun QuestionDraftItem(
    index: Int,
    draft: QuestionDraft,
    onDraftChange: (QuestionDraft) -> Unit,
    onRemove: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Вопрос ${index + 1}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )

                if (onRemove != null) {
                    FilledTonalIconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удалить вопрос"
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = draft.text,
                onValueChange = { onDraftChange(draft.copy(text = it)) },
                label = { Text("Текст вопроса") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(16.dp))

            Text("Тип вопроса", style = MaterialTheme.typography.labelLarge)

            Spacer(Modifier.height(8.dp))

            Row {
                FilterChip(
                    selected = draft.type == QuestionType.SINGLE_CHOICE,
                    onClick = {
                        onDraftChange(
                            draft.copy(
                                type = QuestionType.SINGLE_CHOICE,
                                options = draft.options.map { it.copy(isCorrect = false) }
                            )
                        )
                    },
                    label = { Text("Один ответ") }
                )

                Spacer(Modifier.width(8.dp))

                FilterChip(
                    selected = draft.type == QuestionType.MULTIPLY_CHOICE,
                    onClick = {
                        onDraftChange(
                            draft.copy(type = QuestionType.MULTIPLY_CHOICE)
                        )
                    },
                    label = { Text("Несколько ответов") }
                )
            }


            Spacer(Modifier.height(8.dp))

            Text(
                "Варианты ответа",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.height(8.dp))

            draft.options.forEachIndexed { optIndex, option ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {

                        Checkbox(
                            checked = option.isCorrect,
                            onCheckedChange = { checked ->
                                val newOptions = draft.options.mapIndexed { i, o ->
                                    if (draft.type == QuestionType.SINGLE_CHOICE) {
                                        o.copy(isCorrect = i == optIndex && checked)
                                    } else {
                                        if (i == optIndex)
                                            o.copy(isCorrect = checked)
                                        else o
                                    }
                                }
                                onDraftChange(draft.copy(options = newOptions))
                            }
                        )

                        OutlinedTextField(
                            value = option.text,
                            onValueChange = { text ->
                                val newOptions = draft.options.toMutableList()
                                newOptions[optIndex] =
                                    option.copy(text = text)
                                onDraftChange(
                                    draft.copy(options = newOptions)
                                )
                            },
                            placeholder = {
                                Text("Вариант ${optIndex + 1}")
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        if (draft.options.size > 2) {
                            IconButton(
                                onClick = {
                                    val newOptions =
                                        draft.options.toMutableList()
                                            .also { it.removeAt(optIndex) }
                                    onDraftChange(
                                        draft.copy(options = newOptions)
                                    )
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Удалить вариант"
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    onDraftChange(
                        draft.copy(
                            options = draft.options + OptionDraft()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Добавить вариант")
            }
        }
    }
}