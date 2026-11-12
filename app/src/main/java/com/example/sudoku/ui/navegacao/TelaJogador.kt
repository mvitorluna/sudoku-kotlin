package com.example.sudoku.ui.navegacao

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sudoku.data.local.AppDatabase
import com.example.sudoku.data.repository.ConquistaRepository
import com.example.sudoku.ui.conquistas.AbaConquistas
import com.example.sudoku.ui.conquistas.ConquistasViewModel
import com.example.sudoku.ui.conquistas.ConquistasViewModelFactory
import com.example.sudoku.ui.desafios.AbaDesafios
import com.example.sudoku.ui.historico.AbaHistorico
import com.example.sudoku.ui.novojogo.AbaJogar
import com.example.sudoku.ui.ranking.AbaRanking

@Composable
fun TelaJogador(
    onVoltar: () -> Unit,
    onAbrirJogo: (String, Long) -> Unit,
    onAbrirDesafio: (String) -> Unit,
    conquistasViewModel: ConquistasViewModel = viewModel(
        factory = ConquistasViewModelFactory(
            ConquistaRepository(
                AppDatabase.getDatabase(LocalContext.current).conquistaDAO(),
                AppDatabase.getDatabase(LocalContext.current).partidaDAO()
            )
        )
    )
) {
    val navInterno = rememberNavController()
    val conquistasUiState by conquistasViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BarraSuperior(titulo = "Sudoku", onVoltar = onVoltar) },
        bottomBar = {
            BottomNavJogador(
                navInterno = navInterno,
                temConquistaNova = conquistasUiState.temNovaNaoVista
            )
        }
    ) { padding ->
        NavHost(
            navController = navInterno,
            startDestination = RotasJogador.ABA_JOGAR,
            modifier = Modifier.padding(padding)
        ) {
            composable(RotasJogador.ABA_JOGAR) { AbaJogar(onAbrirJogo = onAbrirJogo) }
            composable(RotasJogador.ABA_DESAFIOS) { AbaDesafios(onAbrirDesafio = onAbrirDesafio) }
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
        TelaJogador(onVoltar = {}, onAbrirJogo = { _, _ -> }, onAbrirDesafio = {})
    }
}
