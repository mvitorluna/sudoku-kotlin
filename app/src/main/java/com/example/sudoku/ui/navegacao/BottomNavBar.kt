package com.example.sudoku.ui.navegacao

// ╔══════════════════════════════════════════════╗
// ║  BottomNavBar.kt                             ║
// ║  Barras de navegação inferior                ║
// ║  Jogador (5 abas) · Admin (2 abas)           ║
// ║  Destaca automaticamente a aba ativa         ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Troca de aba sem empilhar: o "voltar" do celular não passa por todas as abas visitadas
fun navegarParaAba(navInterno: NavHostController, rota: String) {
    navInterno.navigate(rota) {
        popUpTo(navInterno.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// ------ BARRA DO JOGADOR ------
@Composable
fun BottomNavBarJogador(navInterno: NavHostController, temConquistaNova: Boolean = false) {

    // Observa qual rota está ativa (atualiza automaticamente)
    val backStackEntry by navInterno.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    NavigationBar {

        // Aba 1: Jogar
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_JOGAR,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_JOGAR) },
            icon     = { Icon(Icons.Default.PlayArrow, contentDescription = "Jogar") },
            label    = { Text("Jogar") }
        )

        // Aba 2: Desafios
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_DESAFIOS,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_DESAFIOS) },
            icon     = { Icon(Icons.Default.Flag, contentDescription = "Desafios") },
            label    = { Text("Desafios") }
        )

        // Aba 3: Histórico
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_HISTORICO,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_HISTORICO) },
            icon     = { Icon(Icons.Default.History, contentDescription = "Histórico") },
            label    = { Text("Histórico") }
        )

        // Aba 4: Ranking
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_RANKING,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_RANKING) },
            icon     = { Icon(Icons.Default.Leaderboard, contentDescription = "Ranking") },
            label    = { Text("Ranking") }
        )

        // Aba 5: Conquistas (com bolinha de aviso quando tem conquista nova)
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_CONQUISTAS,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_CONQUISTAS) },
            icon     = {
                BadgedBox(badge = { if (temConquistaNova) Badge() }) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = "Conquistas")
                }
            },
            label    = { Text("Conquistas") }
        )
    }
}

// ------ BARRA DO ADMIN ------
@Composable
fun BottomNavBarAdmin(navInterno: NavHostController) {

    // Observa qual rota está ativa (atualiza automaticamente)
    val backStackEntry by navInterno.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    NavigationBar {

        // Aba 1: Gerenciar Desafios
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_ADMIN_DESAFIOS,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_ADMIN_DESAFIOS) },
            icon     = { Icon(Icons.Default.Flag, contentDescription = "Desafios") },
            label    = { Text("Desafios") }
        )

        // Aba 2: Moderar Ranking
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_ADMIN_RANKING,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_ADMIN_RANKING) },
            icon     = { Icon(Icons.Default.Leaderboard, contentDescription = "Ranking") },
            label    = { Text("Ranking") }
        )
    }
}

// ── Previews ─────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun BottomNavBarJogadorPreview() {
    MaterialTheme {
        BottomNavBarJogador(navInterno = rememberNavController(), temConquistaNova = true)
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarAdminPreview() {
    MaterialTheme {
        BottomNavBarAdmin(navInterno = rememberNavController())
    }
}
