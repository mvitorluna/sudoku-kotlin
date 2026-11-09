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
import com.example.sudoku.ui.desafios.AbaGerenciarDesafios
import com.example.sudoku.ui.ranking.AbaModerarRanking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdmin(onVoltar: () -> Unit) {
    val navInterno = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin") },
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
        bottomBar = { BottomNavAdmin(navInterno = navInterno) }
    ) { padding ->
        NavHost(
            navController = navInterno,
            startDestination = RotasAdmin.ABA_ADMIN_DESAFIOS,
            modifier = Modifier.padding(padding)
        ) {
            composable(RotasAdmin.ABA_ADMIN_DESAFIOS) { AbaGerenciarDesafios() }
            composable(RotasAdmin.ABA_ADMIN_RANKING) { AbaModerarRanking() }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaAdminPreview() {
    MaterialTheme {
        TelaAdmin(onVoltar = {})
    }
}
