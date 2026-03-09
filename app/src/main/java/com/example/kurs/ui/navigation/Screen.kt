package com.example.kurs.ui.navigation

sealed class Screen(val route: String) {
    object Splash   : Screen("splash")
    object Login    : Screen("login")
    object Register : Screen("register")
    object Home     : Screen("home")
    object Create   : Screen("create")
    object Profile  : Screen("profile")
    object Play     : Screen("play/{quizId}") {
        fun createRoute(quizId: Long) = "play/$quizId"
    }
    object Result   : Screen("result/{quizId}") {
        fun createRoute(quizId: Long) = "result/$quizId"
    }
    object Study    : Screen("study/{quizId}") {
        fun createRoute(quizId: Long) = "study/$quizId"
    }
}