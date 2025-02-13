package com.example.playerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.playerapp.presentation.screen.MainScreen
import com.example.playerapp.presentation.viewmodel.PlayerViewModel
import com.example.playerapp.ui.theme.PlayerAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayerAppTheme {
                MainScreen(playerViewModel = playerViewModel)
            }
        }
    }
}

