package com.example.kurs.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kurs.data.db.entity.QuizEntity
import com.example.kurs.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val quizzes by viewModel.quizzes.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val createdTitle by savedStateHandle
        ?.getStateFlow<String?>("quiz_created_title", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(createdTitle) {
        if (!createdTitle.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = "Тест «$createdTitle» успешно создан!",
                duration = SnackbarDuration.Short
            )
            savedStateHandle?.set("quiz_created_title", null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Тесты") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = "Профиль")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Create.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Создать тест")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        if (quizzes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Тестов пока нет. Создай первый!", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quizzes) { quiz ->
                    QuizCard(
                        quiz = quiz,
                        isOwner = quiz.authorId == currentUserId,
                        onPlayClick = {
                            navController.navigate(Screen.Play.createRoute(quiz.id))
                        },
                        onStudyClick = {
                            navController.navigate(Screen.Study.createRoute(quiz.id))
                        },
                        onDeleteClick = {
                            val title = quiz.title
                            viewModel.deleteQuiz(quiz)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Тест «$title» удалён",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuizCard(
    quiz: QuizEntity,
    isOwner: Boolean,
    onPlayClick: () -> Unit,
    onStudyClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quiz.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = quiz.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isOwner) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (quiz.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = quiz.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Тест")
                }
                OutlinedButton(
                    onClick = onStudyClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Обучение")
                }
            }
        }
    }
}