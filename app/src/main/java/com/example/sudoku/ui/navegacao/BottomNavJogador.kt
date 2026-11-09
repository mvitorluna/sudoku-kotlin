package com.example.sudoku.ui.navegacao

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private data class AbaJogador(val rota: String, val titulo: String, val icone: ImageVector)

private val abasJogador = listOf(
    AbaJogador(RotasJogador.ABA_JOGAR, "Jogar", Icons.Default.PlayArrow),
    AbaJogador(RotasJogador.ABA_DESAFIOS, "Desafios", Icons.Default.Flag),
    AbaJogador(RotasJogador.ABA_HISTORICO, "Histórico", Icons.Default.History),
    AbaJogador(RotasJogador.ABA_RANKING, "Ranking", Icons.Default.Leaderboard),
    AbaJogador(RotasJogador.ABA_CONQUISTAS, "Conquistas", Icons.Default.EmojiEvents)
)

fun NavHostController.irParaAba(rota: String) {
    navigate(rota) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun BottomNavJogador(navInterno: NavHostController) {
    val backStackEntry by navInterno.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    NavigationBar {
        abasJogador.forEach { aba ->
            NavigationBarItem(
                selected = rotaAtual == aba.rota,
                onClick = { navInterno.irParaAba(aba.rota) },
                icon = { Icon(aba.icone, contentDescription = aba.titulo) },
                label = { Text(aba.titulo) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavJogadorPreview() {
    MaterialTheme {
        BottomNavJogador(navInterno = rememberNavController())
    }
}
