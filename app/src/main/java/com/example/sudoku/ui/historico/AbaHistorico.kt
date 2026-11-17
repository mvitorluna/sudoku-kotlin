package com.example.sudoku.ui.historico

// ╔══════════════════════════════════════════════╗
// ║  AbaHistorico.kt                             ║
// ║  ABA Histórico (jogador)                     ║
// ║  Sub-abas Partidas e Estatísticas            ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

private val subAbas = listOf("Partidas", "Estatísticas")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbaHistorico() {
    var abaSelecionada by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = abaSelecionada) {
            subAbas.forEachIndexed { indice, titulo ->
                Tab(
                    selected = abaSelecionada == indice,
                    onClick = { abaSelecionada = indice },
                    text = { Text(titulo) }
                )
            }
        }

        when (abaSelecionada) {
            0 -> ListaPartidas()
            else -> PainelEstatisticas()
        }
    }
}

// ── Preview ──────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AbaHistoricoPreview() {
    MaterialTheme {
        AbaHistorico()
    }
}
