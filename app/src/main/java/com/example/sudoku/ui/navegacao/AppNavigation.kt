package com.example.sudoku.ui.navegacao

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sudoku.ui.desafios.TelaDesafioJogo
import com.example.sudoku.ui.jogo.TelaJogo

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rotas.INICIO
    ) {
        composable(Rotas.INICIO) {
            TelaInicial(
                onJogar = { navController.navigate(Rotas.JOGADOR) },
                onAdmin = { navController.navigate(Rotas.ADMIN) }
            )
        }

        composable(Rotas.JOGADOR) {
            TelaJogador(
                onVoltar = { navController.popBackStack() },
                onAbrirJogo = { nivel, semente ->
                    navController.navigate(Rotas.jogo(nivel, semente))
                },
                onAbrirDesafio = { desafioId ->
                    navController.navigate(Rotas.desafioJogo(desafioId, System.currentTimeMillis()))
                }
            )
        }

        composable(Rotas.ADMIN) {
            TelaAdmin(onVoltar = { navController.popBackStack() })
        }

        composable(Rotas.JOGO) { backStackEntry ->
            val nivel = backStackEntry.arguments?.getString("nivel") ?: "FACIL"
            val semente = backStackEntry.arguments?.getString("semente")?.toLongOrNull() ?: 0L
            TelaJogo(
                nivel = nivel,
                semente = semente,
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(Rotas.DESAFIO_JOGO) { backStackEntry ->
            val desafioId = backStackEntry.arguments?.getString("desafioId") ?: ""
            val semente = backStackEntry.arguments?.getString("semente")?.toLongOrNull() ?: 0L
            TelaDesafioJogo(
                desafioId = desafioId,
                semente = semente,
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}
