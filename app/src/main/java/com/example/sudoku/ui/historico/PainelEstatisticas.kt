package com.example.sudoku.ui.historico

// ╔══════════════════════════════════════════════╗
// ║  PainelEstatisticas.kt                       ║
// ║  Sub-aba Estatísticas                        ║
// ║  Vitórias, tempos e pontos por nível         ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.data.local.AppDatabase
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.logic.Nivel

@Composable
fun PainelEstatisticas(
    viewModel: EstatisticasViewModel = viewModel(
        factory = EstatisticasViewModelFactory(
            PartidaRepository(AppDatabase.getDatabase(LocalContext.current).partidaDAO())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConteudoEstatisticas(
        uiState = uiState,
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@Composable
fun ConteudoEstatisticas(
    uiState: EstatisticasUiState,
    onErroVisto: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.erro != null) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = uiState.erro,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onErroVisto) {
                        Text("OK")
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UmNumero("Partidas", uiState.totalPartidas.toString(), Modifier.weight(1f))
                UmNumero("Vitórias", uiState.vitorias.toString(), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UmNumero("% de vitória", "${uiState.percentualVitorias}%", Modifier.weight(1f))
                UmNumero("Sequência atual", uiState.sequenciaAtual.toString(), Modifier.weight(1f))
            }
        }

        item {
            Text("Por nível", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(uiState.porNivel) { estatistica ->
            UmNivelEstatistica(estatistica)
        }
    }
}

@Composable
fun UmNumero(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun UmNivelEstatistica(estatistica: EstatisticaNivel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${estatistica.nivel.titulo} ${estatistica.nivel.tamanho}x${estatistica.nivel.tamanho}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Jogadas: ${estatistica.jogadas} | Vitórias: ${estatistica.vitorias}")
            Text("Melhor tempo: ${estatistica.textoMelhorTempo}")
            Text("Média de pontos (vitórias): ${estatistica.textoMediaPontos}")
        }
    }
}

// ── Preview ──────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConteudoEstatisticasPreview() {
    MaterialTheme {
        ConteudoEstatisticas(
            uiState = EstatisticasUiState(
                totalPartidas = 12,
                vitorias = 9,
                sequenciaAtual = 3,
                porNivel = listOf(
                    EstatisticaNivel(Nivel.FACIL, 6, 5, 42, 870.0),
                    EstatisticaNivel(Nivel.MEDIO, 4, 3, 210, 1650.5),
                    EstatisticaNivel(Nivel.DIFICIL, 2, 1, 780, 2100.0)
                )
            ),
            onErroVisto = {}
        )
    }
}
