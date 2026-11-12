package com.example.sudoku.ui.novojogo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.data.local.AppDatabase
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.logic.Nivel

@Composable
fun AbaJogar(
    onAbrirJogo: (String, Long) -> Unit,
    viewModel: NovoJogoViewModel = viewModel(
        factory = NovoJogoViewModelFactory(
            PartidaRepository(AppDatabase.getDatabase(LocalContext.current).partidaDAO())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConteudoJogar(
        uiState = uiState,
        onSelecionarNivel = { viewModel.onSelecionarNivel(it) },
        onJogar = { onAbrirJogo(uiState.nivelSelecionado.name, 0L) },
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@Composable
fun ConteudoJogar(
    uiState: NovoJogoUiState,
    onSelecionarNivel: (Nivel) -> Unit,
    onJogar: () -> Unit,
    onErroVisto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (uiState.erro != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.erro,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onErroVisto) {
                    Text("OK")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = "Escolha o Nível",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.resumos) { resumo ->
                UmNivel(
                    resumo = resumo,
                    selecionado = resumo.nivel == uiState.nivelSelecionado,
                    onClick = { onSelecionarNivel(resumo.nivel) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onJogar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = uiState.textoBotao)
        }
    }
}

@Composable
fun UmNivel(
    resumo: ResumoNivel,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val border = if (selecionado) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }

    val containerColor = if (selecionado) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        border = border,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = resumo.nivel.nomeComTamanho,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Partidas: ${resumo.partidasJogadas} | Vitórias: ${resumo.vitorias}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (resumo.jaJogou) {
                Text(
                    text = "Recorde: ${resumo.textoRecorde} | Melhor tempo: ${resumo.textoMelhorTempo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConteudoJogarPreview() {
    ConteudoJogar(
        uiState = NovoJogoUiState(),
        onSelecionarNivel = {},
        onJogar = {},
        onErroVisto = {}
    )
}
