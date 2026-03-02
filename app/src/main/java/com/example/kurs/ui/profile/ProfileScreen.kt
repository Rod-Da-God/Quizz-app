package com.example.kurs.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.kurs.data.db.entity.AchievementEntity
import com.example.kurs.data.db.entity.AchievementType
import com.example.kurs.data.db.entity.QuizResultEntity
import com.example.kurs.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val loggedOut by viewModel.loggedOut.collectAsState()

    LaunchedEffect(loggedOut) {
        if (loggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User info card
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = state.user?.username ?: "—",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = state.user?.email ?: "—",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                StatItem(label = "Тестов", value = state.results.size.toString())
                                StatItem(
                                    label = "Лучший",
                                    value = if (state.results.isEmpty()) "—"
                                    else "%.0f%%".format(state.results.maxOf { it.overallPercent })
                                )
                                StatItem(
                                    label = "Наград",
                                    value = state.achievements.size.toString()
                                )
                            }
                        }
                    }
                }

                // Achievements
                item {
                    Text("Достижения", style = MaterialTheme.typography.titleMedium)
                }

                item {
                    val allTypes = AchievementType.values()
                    val unlockedTypes = state.achievements.map { it.type }.toSet()

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allTypes.forEach { type ->
                            val unlocked = type in unlockedTypes
                            AchievementItem(type = type, unlocked = unlocked)
                        }
                    }
                }

                // Results history
                if (state.results.isNotEmpty()) {
                    item {
                        Text("История результатов", style = MaterialTheme.typography.titleMedium)
                    }

                    items(state.results) { result ->
                        ResultHistoryItem(result = result)
                    }
                }

                // Logout
                item {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Выйти из аккаунта")
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AchievementItem(type: AchievementType, unlocked: Boolean) {
    val (emoji, title, description) = achievementInfo(type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (unlocked) emoji else "🔒",
                style = MaterialTheme.typography.headlineSmall
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (unlocked)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (unlocked)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ResultHistoryItem(result: QuizResultEntity) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    val date = dateFormat.format(Date(result.completedAt))
    val total = result.easyTotal + result.mediumTotal + result.hardTotal
    val correct = result.easyCorrect + result.mediumCorrect + result.hardCorrect

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = date, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "$correct / $total вопросов",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "%.0f%%".format(result.overallPercent),
                style = MaterialTheme.typography.titleMedium,
                color = when {
                    result.overallPercent >= 80f -> MaterialTheme.colorScheme.tertiary
                    result.overallPercent >= 50f -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

private data class AchievementMeta(val emoji: String, val title: String, val description: String)

private fun achievementInfo(type: AchievementType): AchievementMeta = when (type) {
    AchievementType.FIRST_QUIZ     -> AchievementMeta("🎉", "Первый тест", "Пройди свой первый тест")
    AchievementType.PERFECT_SCORE  -> AchievementMeta("🏆", "Идеальный результат", "Ответь правильно на все вопросы")
    AchievementType.HARD_BLOCK_DONE -> AchievementMeta("💪", "Сложный уровень", "Пройди сложный блок без ошибок")
    AchievementType.SPEED_RUNNER   -> AchievementMeta("⚡", "Спидраннер", "Отвечай быстрее 10 секунд на вопрос")
    AchievementType.COLLECTOR      -> AchievementMeta("📚", "Коллекционер", "Собери все достижения")
    AchievementType.HALF_WAY       -> AchievementMeta("⭐", "На полпути", "Набери 50% и выше")
    AchievementType.DEDICATED      -> AchievementMeta("🔥", "Преданный", "Пройди 10 тестов")
}