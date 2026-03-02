package com.example.kurs.ui.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.kurs.data.db.entity.QuizResultEntity
import com.example.kurs.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    navController: NavController,
    quizId: Long,
    viewModel: QuizResultViewModel = hiltViewModel()
) {
    val result by viewModel.result.collectAsState()

    LaunchedEffect(quizId) { viewModel.loadResult(quizId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результаты") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "На главную")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (result == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                ResultContent(result = result!!, onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                })
            }
        }
    }
}

@Composable
fun ResultContent(result: QuizResultEntity, onHome: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Тест завершён!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Общий результат", style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(
                        progress = { result.overallPercent / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "%.1f%%".format(result.overallPercent),
                        style = MaterialTheme.typography.headlineSmall,
                        color = when {
                            result.overallPercent >= 80f -> MaterialTheme.colorScheme.tertiary
                            result.overallPercent >= 50f -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }

        item {
            BlockResultCard(
                title = "Лёгкий уровень",
                correct = result.easyCorrect,
                total = result.easyTotal,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        item {
            BlockResultCard(
                title = "Средний уровень",
                correct = result.mediumCorrect,
                total = result.mediumTotal,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        item {
            BlockResultCard(
                title = "Сложный уровень",
                correct = result.hardCorrect,
                total = result.hardTotal,
                color = MaterialTheme.colorScheme.error
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Время", style = MaterialTheme.typography.titleMedium)
                    val minutes = result.timeSpentSeconds / 60
                    val seconds = result.timeSpentSeconds % 60
                    Text(
                        text = "%02d:%02d".format(minutes, seconds),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        item {
            Button(
                onClick = onHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("На главную")
            }
        }
    }
}

@Composable
fun BlockResultCard(title: String, correct: Int, total: Int, color: Color) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "$correct / $total",
                    style = MaterialTheme.typography.titleSmall,
                    color = color
                )
            }
            if (total > 0) {
                LinearProgressIndicator(
                    progress = { correct.toFloat() / total },
                    modifier = Modifier.fillMaxWidth(),
                    color = color
                )
            }
        }
    }
}