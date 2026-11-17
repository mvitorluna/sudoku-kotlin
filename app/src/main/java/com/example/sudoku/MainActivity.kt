package com.example.sudoku

// ╔══════════════════════════════════════════════╗
// ║  MainActivity.kt                             ║
// ║  PONTO DE ENTRADA do aplicativo              ║
// ║  Chama AppNavigation() que controla as telas ║
// ╚══════════════════════════════════════════════╝

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.sudoku.ui.navegacao.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // Ponto de entrada da navegação
                AppNavigation()
            }
        }
    }
}
