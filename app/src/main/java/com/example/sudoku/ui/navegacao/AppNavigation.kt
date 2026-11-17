package com.example.sudoku.ui.navegacao

// ╔══════════════════════════════════════════════╗
// ║  AppNavigation.kt                            ║
// ║  Define TODAS as rotas do app (NavHost)      ║
// ║  Conecta cada rota ao Composable correto     ║
// ╚══════════════════════════════════════════════╝

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
    // Cria o controlador de navegação (gerencia pilha de telas)
    val navController = rememberNavController()

    // Diferente da aula, não criamos ViewModel aqui:
    // cada tela cria o seu pela Factory, com o Repository que precisa

    // NavHost = "mapa" de todas as telas do app
    NavHost(
        navController = navController,
        startDestination = Rotas.HOME,  // tela que abre primeiro
        // Animação: tela nova entra pela direita; ao voltar, ela sai pela direita
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it / 3 } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it / 3 } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {

        // Rota: "home" → abre TelaPrincipal
        composable(Rotas.HOME) {
            TelaPrincipal(navController = navController)
        }

        // Rota: "jogador" → abre TelaJogador (TopBar + BottomNavigation com 5 abas)
        composable(Rotas.JOGADOR) {
            TelaJogador(navController = navController)
        }

        // Rota: "admin" → abre TelaAdmin (TopBar + BottomNavigation com 2 abas)
        composable(Rotas.ADMIN) {
            TelaAdmin(navController = navController)
        }

        // Rota: "jogo/{nivel}/{semente}" → recebe argumentos pela URL
        composable(Rotas.JOGO) { backStackEntry ->
            val nivel = backStackEntry.arguments?.getString("nivel") ?: "FACIL"
            val semente = backStackEntry.arguments?.getString("semente")?.toLongOrNull() ?: 0L
            TelaJogo(
                nivel = nivel,
                semente = semente,
                onVoltar = { navController.popBackStack() }
            )
        }

        // Rota: "desafio_jogo/{desafioId}/{semente}" → recebe argumentos pela URL
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
