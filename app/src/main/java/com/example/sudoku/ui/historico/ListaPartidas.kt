package com.example.sudoku.ui.historico

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.logic.Nivel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ListaPartidas(
    viewModel: HistoricoViewModel = viewModel(
        factory = HistoricoViewModelFactory(
            PartidaRepository(AppDatabase.getDatabase(LocalContext.current).partidaDAO())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConteudoPartidas(
        uiState = uiState,
        onFiltroNivelChange = { viewModel.onFiltroNivelChange(it) },
        onSoVitoriasChange = { viewModel.onSoVitoriasChange() },
        onSoFavoritasChange = { viewModel.onSoFavoritasChange() },
        onSoDesafiosChange = { viewModel.onSoDesafiosChange() },
        onOrdenacaoChange = { viewModel.onOrdenacaoChange(it) },
        onPartidaClick = { viewModel.onPartidaClick(it) },
        onFavoritar = { viewModel.onFavoritar(it) },
        onDelete = { viewModel.onDelete(it) },
        onPedirLimpar = { viewModel.onPedirLimpar() },
        onCancelarLimpar = { viewModel.onCancelarLimpar() },
        onConfirmarLimpar = { viewModel.onConfirmarLimpar() },
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@Composable
fun ConteudoPartidas(
    uiState: HistoricoUiState,
    onFiltroNivelChange: (Nivel?) -> Unit,
    onSoVitoriasChange: () -> Unit,
    onSoFavoritasChange: () -> Unit,
    onSoDesafiosChange: () -> Unit,
    onOrdenacaoChange: (OrdenacaoPartidas) -> Unit,
    onPartidaClick: (Partida) -> Unit,
    onFavoritar: (Partida) -> Unit,
    onDelete: (Partida) -> Unit,
    onPedirLimpar: () -> Unit,
    onCancelarLimpar: () -> Unit,
    onConfirmarLimpar: () -> Unit,
    onErroVisto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${uiState.listaDePartidas.size} partidas", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onPedirLimpar) {
                Text("Limpar histórico")
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.filtroNivel == null,
                onClick = { onFiltroNivelChange(null) },
                label = { Text("Todos") }
            )
            Nivel.entries.forEach { nivel ->
                FilterChip(
                    selected = uiState.filtroNivel == nivel,
                    onClick = { onFiltroNivelChange(nivel) },
                    label = { Text(nivel.titulo) }
                )
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = uiState.soVitorias, onClick = onSoVitoriasChange, label = { Text("Vitórias") })
            FilterChip(selected = uiState.soFavoritas, onClick = onSoFavoritasChange, label = { Text("Favoritas") })
            FilterChip(selected = uiState.soDesafios, onClick = onSoDesafiosChange, label = { Text("Desafios") })
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OrdenacaoPartidas.entries.forEach { ordenacao ->
                FilterChip(
                    selected = uiState.ordenacao == ordenacao,
                    onClick = { onOrdenacaoChange(ordenacao) },
                    label = { Text(ordenacao.titulo) }
                )
            }
        }

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
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.listaDePartidas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.textoListaVazia, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.listaDePartidas, key = { it.id }) { partida ->
                    UmaPartida(
                        partida = partida,
                        expandida = uiState.partidaExpandidaId == partida.id,
                        onClick = { onPartidaClick(partida) },
                        onFavoritar = { onFavoritar(partida) },
                        onDelete = { onDelete(partida) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }

    if (uiState.confirmandoLimpar) {
        AlertDialog(
            onDismissRequest = onCancelarLimpar,
            title = { Text("Limpar histórico") },
            text = { Text("Todas as partidas salvas serão apagadas. Deseja continuar?") },
            confirmButton = {
                TextButton(onClick = onConfirmarLimpar) {
                    Text("Limpar")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelarLimpar) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UmaPartida(
    partida: Partida,
    expandida: Boolean,
    onClick: () -> Unit,
    onFavoritar: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nivel = Nivel.deTexto(partida.nivel)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${nivel.titulo} ${nivel.tamanho}x${nivel.tamanho}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (partida.venceu) "Vitória · ${partida.pontos} pts" else "Derrota",
                        color = if (partida.venceu) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatarData(partida.data),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onFavoritar) {
                    Icon(
                        imageVector = if (partida.favorita) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = if (partida.favorita) "Desfavoritar" else "Favoritar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir partida")
                }
            }

            AnimatedVisibility(visible = expandida) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 0.dp))
                    Text("Tempo: ${formatarTempo(partida.tempoSegundos)}")
                    Text("Erros: ${partida.erros}")
                    Text("Dicas usadas: ${partida.dicasUsadas}")
                    Text(if (partida.semente != 0L) "Desafio (semente ${partida.semente})" else "Jogo aleatório")
                }
            }
        }
    }
}

private fun formatarData(data: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(data))

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConteudoPartidasPreview() {
    val partidas = listOf(
        Partida(id = 1, nivel = "FACIL", venceu = true, tempoSegundos = 45, erros = 0, dicasUsadas = 1, pontos = 855, favorita = true),
        Partida(id = 2, nivel = "MEDIO", venceu = false, tempoSegundos = 300, erros = 3, dicasUsadas = 0, pontos = 0),
        Partida(id = 3, nivel = "DIFICIL", venceu = true, tempoSegundos = 900, erros = 1, dicasUsadas = 0, pontos = 2050, semente = 48213)
    )
    MaterialTheme {
        ConteudoPartidas(
            uiState = HistoricoUiState(listaDePartidas = partidas, partidaExpandidaId = 1),
            onFiltroNivelChange = {},
            onSoVitoriasChange = {},
            onSoFavoritasChange = {},
            onSoDesafiosChange = {},
            onOrdenacaoChange = {},
            onPartidaClick = {},
            onFavoritar = {},
            onDelete = {},
            onPedirLimpar = {},
            onCancelarLimpar = {},
            onConfirmarLimpar = {},
            onErroVisto = {}
        )
    }
}
