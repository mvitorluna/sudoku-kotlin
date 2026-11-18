package com.example.sudoku.ui.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.data.remote.RankingFirestore
import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.logic.Nivel

@Composable
fun AbaRanking(
    viewModel: RankingViewModel = viewModel(
        factory = RankingViewModelFactory(
            RankingRepository(
                RankingFirestore()
            )
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConteudoRanking(
        uiState = uiState,
        onFiltroNivel = { viewModel.onFiltroNivelChange(it) },
        onBuscaNome = { viewModel.onBuscaNomeChange(it) },
        onTentarNovamente = { viewModel.carregarRanking() },
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@Composable
fun ConteudoRanking(
    uiState: RankingUiState,
    onFiltroNivel: (Nivel?) -> Unit,
    onBuscaNome: (String) -> Unit,
    onTentarNovamente: () -> Unit,
    onErroVisto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ranking Global (Top 20)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.buscaNome,
            onValueChange = onBuscaNome,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar jogador por nome...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.erro != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiState.erro,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onTentarNovamente) {
                        Text("Tentar de novo")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.carregando) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.itensRanking.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum jogador encontrado no ranking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.itensRanking) { itemUi ->
                    ItemRankingCard(itemUi)
                }
            }
        }
    }
}

@Composable
fun ItemRankingCard(itemUi: RankingItemUi) {
    val containerColor = when (itemUi.posicao) {
        1 -> Color(0xFFFFF9C4)
        2 -> Color(0xFFF5F5F5)
        3 -> Color(0xFFFFE0B2)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (itemUi.posicao) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = itemUi.medalha ?: "${itemUi.posicao}º",
                    fontWeight = FontWeight.Bold,
                    color = if (itemUi.posicao <= 3) Color.DarkGray else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = itemUi.item.nomeJogador,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nível: ${itemUi.item.nivel} | Tempo: ${itemUi.item.tempoSegundos}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${itemUi.item.pontos} pts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConteudoRankingPreview() {
    MaterialTheme {
        ConteudoRanking(
            uiState = RankingUiState(
                carregando = false,
                itensRanking = listOf(
                    RankingItemUi(1, com.example.sudoku.data.remote.RankingRemoto("1", "Carlos Silva", "FACIL", 1250, 45)),
                    RankingItemUi(2, com.example.sudoku.data.remote.RankingRemoto("2", "Ana Souza", "MEDIO", 1100, 60))
                )
            ),
            onFiltroNivel = {},
            onBuscaNome = {},
            onTentarNovamente = {},
            onErroVisto = {}
        )
    }
}
