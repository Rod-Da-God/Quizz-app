package com.example.kurs.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kurs.ui.auth.login.LoginScreen
import com.example.kurs.ui.auth.register.RegisterScreen
import com.example.kurs.ui.create.CreateQuizScreen
import com.example.kurs.ui.home.HomeScreen
import com.example.kurs.ui.play.QuizPlayScreen
import com.example.kurs.ui.play.QuizResultScreen
import com.example.kurs.ui.profile.ProfileScreen
import com.example.kurs.ui.splash.SplashScreen
import com.example.kurs.ui.study.StudyScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Create.route) {
            CreateQuizScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Play.route) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")?.toLongOrNull() ?: 0L
            QuizPlayScreen(navController = navController, quizId = quizId)
        }
        composable(Screen.Result.route) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")?.toLongOrNull() ?: 0L
            QuizResultScreen(navController = navController, quizId = quizId)
        }
        composable(Screen.Study.route) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")?.toLongOrNull() ?: 0L
            StudyScreen(navController = navController, quizId = quizId)
        }
    }
}