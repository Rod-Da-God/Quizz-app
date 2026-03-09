package com.example.kurs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kurs.ui.navigation.AppNavGraph
import com.example.kurs.ui.theme.KursTheme
import dagger.hilt.android.AndroidEntryPoint
import android.media.MediaPlayer

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KursTheme {
                AppNavGraph()
            }
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}