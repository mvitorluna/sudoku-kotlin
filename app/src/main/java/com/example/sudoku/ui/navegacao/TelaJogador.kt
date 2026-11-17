package com.example.sudoku.ui.navegacao

// ╔══════════════════════════════════════════════╗
// ║  TelaJogador.kt                              ║
// ║  TELA 2: TopBar + BottomNavigation + Abas    ║
// ║  (a TelaComAbas da aula, com 5 abas)         ║
// ║  Tem seu próprio NavController interno       ║
// ╚══════════════════════════════════════════════╝

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaJogador(
    navController: NavHostController,
    // Só para a bolinha de "conquista nova" na barra de baixo
    conquistasViewModel: ConquistasViewModel = viewModel(
        factory = ConquistasViewModelFactory(
            ConquistaRepository(
                AppDatabase.getDatabase(LocalContext.current).conquistaDAO(),
                AppDatabase.getDatabase(LocalContext.current).partidaDAO()
            )
        )
    )
) {

    // NavController INTERNO — só para navegar entre as abas
    val navInterno = rememberNavController()
    val conquistasUiState by conquistasViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sudoku") },
                navigationIcon = {
                    // Botão de voltar — usa o navController PRINCIPAL
                    IconButton(onClick = { navController.popBackStack() }) {
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
        bottomBar = {
            // Barra de navegação inferior — usa navController INTERNO
            BottomNavBarJogador(
                navInterno = navInterno,
                temConquistaNova = conquistasUiState.temNovaNaoVista
            )
        }
    ) { padding ->

        // NavHost INTERNO — controla qual aba está ativa
        NavHost(
            navController = navInterno,
            startDestination = RotasAbas.ABA_JOGAR,
            modifier = Modifier.padding(padding)
        ) {
            // Abrir um jogo usa o navController PRINCIPAL (a TelaJogo fica fora das abas)
            composable(RotasAbas.ABA_JOGAR) {
                AbaJogar(onAbrirJogo = { nivel, semente ->
                    navController.navigate(Rotas.jogo(nivel, semente))
                })
            }
            composable(RotasAbas.ABA_DESAFIOS) {
                AbaDesafios(onAbrirDesafio = { desafioId ->
                    navController.navigate(Rotas.desafioJogo(desafioId, System.currentTimeMillis()))
                })
            }
            composable(RotasAbas.ABA_HISTORICO)  { AbaHistorico() }
            composable(RotasAbas.ABA_RANKING)    { AbaRanking() }
            composable(RotasAbas.ABA_CONQUISTAS) { AbaConquistas() }
        }
    }
}

// ── Preview ──────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaJogadorPreview() {
    MaterialTheme {
        TelaJogador(navController = rememberNavController())
    }
}
