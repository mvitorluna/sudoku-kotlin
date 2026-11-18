package com.example.sudoku.ui.navegacao

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
        startDestination = Rotas.HOME,
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it / 3 } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it / 3 } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable(Rotas.HOME) {
            TelaPrincipal(navController = navController)
        }

        composable(Rotas.JOGADOR) {
            TelaJogador(navController = navController)
        }

        composable(Rotas.ADMIN) {
            TelaAdmin(navController = navController)
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
