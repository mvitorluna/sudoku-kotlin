package com.example.sudoku.ui.ranking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.data.remote.RankingFirestore
import com.example.sudoku.data.remote.RankingRemoto
import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.logic.Nivel

@Composable
fun AbaModerarRanking(
    viewModel: AdminRankingViewModel = viewModel(
        factory = AdminRankingViewModelFactory(
            RankingRepository(
                RankingFirestore()
            )
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConteudoModerarRanking(
        uiState = uiState,
        onFiltroNivel = { viewModel.onFiltroNivelChange(it) },
        onBuscaNome = { viewModel.onBuscaNomeChange(it) },
        onExcluirClick = { viewModel.onPedirExclusao(it) },
        onConfirmarExclusao = { viewModel.onConfirmarExclusao() },
        onCancelarExclusao = { viewModel.onCancelarExclusao() },
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@Composable
fun ConteudoModerarRanking(
    uiState: AdminRankingUiState,
    onFiltroNivel: (Nivel?) -> Unit,
    onBuscaNome: (String) -> Unit,
    onExcluirClick: (RankingRemoto) -> Unit,
    onConfirmarExclusao: () -> Unit,
    onCancelarExclusao: () -> Unit,
    onErroVisto: () -> Unit
) {
    if (uiState.confirmandoExclusao != null) {
        AlertDialog(
            onDismissRequest = onCancelarExclusao,
            title = { Text("Excluir item do ranking") },
            text = { Text("Deseja realmente excluir a pontuação de '${uiState.confirmandoExclusao.nomeJogador}'?") },
            confirmButton = {
                TextButton(onClick = onConfirmarExclusao) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelarExclusao) {
                    Text("Cancelar")
                }
            }
        )
    }

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
            Text(
                text = "Moderação de Ranking",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Painel Estatístico por Nível",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.estatisticasPorNivel) { estatistica ->
            CardEstatisticaNivel(estatistica)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gerenciar Registros do Ranking",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = uiState.buscaNome,
                onValueChange = onBuscaNome,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Filtrar por nome do jogador...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.filtroNivel == null,
                    onClick = { onFiltroNivel(null) },
                    label = { Text("Todos") }
                )
                Nivel.entries.forEach { nivel ->
                    FilterChip(
                        selected = uiState.filtroNivel == nivel,
                        onClick = { onFiltroNivel(nivel) },
                        label = { Text(nivel.titulo) }
                    )
                }
            }
        }

        if (uiState.carregando) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.listaRanking.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum registro encontrado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(uiState.listaRanking) { item ->
                CardModerarRankingItem(
                    item = item,
                    onExcluir = { onExcluirClick(item) }
                )
            }
        }
    }
}

@Composable
fun CardEstatisticaNivel(estatistica: EstatisticaRankingNivel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${estatistica.nivel.titulo} (${estatistica.nivel.tamanho}x${estatistica.nivel.tamanho})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Total de registros: ${estatistica.totalRegistros}")
            Text("Média de pontos: ${estatistica.textoMediaPontos}")
            Text("Melhor tempo: ${estatistica.textoMelhorTempo}")
            Text("Jogador mais frequente: ${estatistica.maisFrequente ?: "Nenhum"}")
        }
    }
}

@Composable
fun CardModerarRankingItem(
    item: RankingRemoto,
    onExcluir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nomeJogador,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nível: ${item.nivel} | Pontos: ${item.pontos} | Tempo: ${item.tempoSegundos}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onExcluir) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConteudoModerarRankingPreview() {
    MaterialTheme {
        ConteudoModerarRanking(
            uiState = AdminRankingUiState(
                carregando = false,
                estatisticasPorNivel = listOf(
                    EstatisticaRankingNivel(Nivel.FACIL, 5, 1200.0, 45, "Carlos Silva")
                ),
                listaRanking = listOf(
                    RankingRemoto("1", "Carlos Silva", "FACIL", 1250, 45)
                )
            ),
            onFiltroNivel = {},
            onBuscaNome = {},
            onExcluirClick = {},
            onConfirmarExclusao = {},
            onCancelarExclusao = {},
            onErroVisto = {}
        )
    }
}
