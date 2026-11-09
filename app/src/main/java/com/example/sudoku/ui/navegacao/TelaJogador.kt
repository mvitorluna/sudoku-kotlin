package com.example.sudoku.ui.navegacao

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sudoku.ui.conquistas.AbaConquistas
import com.example.sudoku.ui.desafios.AbaDesafios
import com.example.sudoku.ui.historico.AbaHistorico
import com.example.sudoku.ui.novojogo.AbaJogar
import com.example.sudoku.ui.ranking.AbaRanking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaJogador(onVoltar: () -> Unit, onAbrirJogo: (String, Long) -> Unit) {
    val navInterno = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sudoku") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { BottomNavJogador(navInterno = navInterno) }
    ) { padding ->
        NavHost(
            navController = navInterno,
            startDestination = RotasJogador.ABA_JOGAR,
            modifier = Modifier.padding(padding)
        ) {
            composable(RotasJogador.ABA_JOGAR) { AbaJogar(onAbrirJogo = onAbrirJogo) }
            composable(RotasJogador.ABA_DESAFIOS) { AbaDesafios(onAbrirJogo = onAbrirJogo) }
            composable(RotasJogador.ABA_HISTORICO) { AbaHistorico() }
            composable(RotasJogador.ABA_RANKING) { AbaRanking() }
            composable(RotasJogador.ABA_CONQUISTAS) { AbaConquistas() }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaJogadorPreview() {
    MaterialTheme {
        TelaJogador(onVoltar = {}, onAbrirJogo = { _, _ -> })
    }
}
