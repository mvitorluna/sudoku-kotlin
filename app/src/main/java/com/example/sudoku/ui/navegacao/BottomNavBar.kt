package com.example.sudoku.ui.navegacao

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

fun navegarParaAba(navInterno: NavHostController, rota: String) {
    navInterno.navigate(rota) {
        popUpTo(navInterno.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun BottomNavBarJogador(navInterno: NavHostController, temConquistaNova: Boolean = false) {
    val backStackEntry by navInterno.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_JOGAR,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_JOGAR) },
            icon     = { Icon(Icons.Default.PlayArrow, contentDescription = "Jogar") },
            label    = { Text("Jogar") }
        )

        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_DESAFIOS,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_DESAFIOS) },
            icon     = { Icon(Icons.Default.Flag, contentDescription = "Desafios") },
            label    = { Text("Desafios") }
        )

        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_HISTORICO,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_HISTORICO) },
            icon     = { Icon(Icons.Default.History, contentDescription = "Histórico") },
            label    = { Text("Histórico") }
        )

        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_RANKING,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_RANKING) },
            icon     = { Icon(Icons.Default.Leaderboard, contentDescription = "Ranking") },
            label    = { Text("Ranking") }
        )

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

@Composable
fun BottomNavBarAdmin(navInterno: NavHostController) {
    val backStackEntry by navInterno.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_ADMIN_DESAFIOS,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_ADMIN_DESAFIOS) },
            icon     = { Icon(Icons.Default.Flag, contentDescription = "Desafios") },
            label    = { Text("Desafios") }
        )

        NavigationBarItem(
            selected = rotaAtual == RotasAbas.ABA_ADMIN_RANKING,
            onClick  = { navegarParaAba(navInterno, RotasAbas.ABA_ADMIN_RANKING) },
            icon     = { Icon(Icons.Default.Leaderboard, contentDescription = "Ranking") },
            label    = { Text("Ranking") }
        )
    }
}

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
