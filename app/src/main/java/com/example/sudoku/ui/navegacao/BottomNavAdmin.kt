package com.example.sudoku.ui.navegacao

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Leaderboard
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

@Composable
fun BottomNavAdmin(navInterno: NavHostController) {
    val backStackEntry by navInterno.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = rotaAtual == RotasAdmin.ABA_ADMIN_DESAFIOS,
            onClick = { navInterno.irParaAba(RotasAdmin.ABA_ADMIN_DESAFIOS) },
            icon = { Icon(Icons.Default.Flag, contentDescription = "Desafios") },
            label = { Text("Desafios") }
        )

        NavigationBarItem(
            selected = rotaAtual == RotasAdmin.ABA_ADMIN_RANKING,
            onClick = { navInterno.irParaAba(RotasAdmin.ABA_ADMIN_RANKING) },
            icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Ranking") },
            label = { Text("Ranking") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavAdminPreview() {
    MaterialTheme {
        BottomNavAdmin(navInterno = rememberNavController())
    }
}
