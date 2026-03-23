package com.muhammedturgut.esp32marauderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.muhammedturgut.esp32marauderapp.core.appNavigation.AppNavHost
import com.muhammedturgut.esp32marauderapp.presentation.viewModel.MainViewModel
import com.muhammedturgut.esp32marauderapp.ui.theme.Esp32marauderappTheme
import dagger.hilt.android.AndroidEntryPoint

// MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Esp32marauderappTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier
                        .background(color = Color.Black)
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier
                            .background(color = Color.Black)
                            .padding(innerPadding)
                    )
                }
            }
        }
        mainViewModel.requestUsbPermission(this@MainActivity)
    }

}
