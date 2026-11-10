package com.example.sudoku.ui.conquistas

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.sudoku.data.repository.ConquistaRepository
import com.example.sudoku.logic.RegrasConquistas
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AbaConquistas(
    viewModel: ConquistasViewModel = viewModel(
        factory = ConquistasViewModelFactory(
            ConquistaRepository(
                AppDatabase.getDatabase(LocalContext.current).conquistaDAO(),
                AppDatabase.getDatabase(LocalContext.current).partidaDAO()
            )
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onTelaAberta()
    }

    ConteudoConquistas(
        uiState = uiState,
        onFiltroChange = { viewModel.onFiltroChange(it) },
        onPedirReset = { viewModel.onPedirReset() },
        onCancelarReset = { viewModel.onCancelarReset() },
        onConfirmarReset = { viewModel.onConfirmarReset() },
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@Composable
fun ConteudoConquistas(
    uiState: ConquistasUiState,
    onFiltroChange: (FiltroConquista) -> Unit,
    onPedirReset: () -> Unit,
    onCancelarReset: () -> Unit,
    onConfirmarReset: () -> Unit,
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
            Column {
                Text("Conquistas", style = MaterialTheme.typography.titleLarge)
                Text(uiState.textoContador, style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onPedirReset) {
                Text("Resetar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FiltroConquista.entries.forEach { filtro ->
                FilterChip(
                    selected = uiState.filtro == filtro,
                    onClick = { onFiltroChange(filtro) },
                    label = { Text(filtro.titulo) }
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

        if (uiState.listaDeConquistas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.textoListaVazia, textAlign = TextAlign.Center)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.listaDeConquistas) { item ->
                    UmaConquista(item = item)
                }
            }
        }
    }

    if (uiState.confirmandoReset) {
        AlertDialog(
            onDismissRequest = onCancelarReset,
            title = { Text("Resetar conquistas") },
            text = { Text("Todas as conquistas desbloqueadas serão apagadas. Deseja continuar?") },
            confirmButton = {
                TextButton(onClick = onConfirmarReset) {
                    Text("Resetar")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelarReset) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UmaConquista(item: ConquistaItem) {
    val corDestaque = if (item.desbloqueada) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, corDestaque),
        colors = CardDefaults.cardColors(
            containerColor = if (item.desbloqueada) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (item.desbloqueada) Icons.Default.EmojiEvents else Icons.Default.Lock,
                contentDescription = item.regra.titulo,
                tint = corDestaque,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.regra.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = item.regra.descricao,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (item.desbloqueadaEm != null) {
                Text(
                    text = formatarData(item.desbloqueadaEm),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LinearProgressIndicator(
                    progress = { item.percentual },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.textoProgresso, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

private fun formatarData(data: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(data))

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConteudoConquistasPreview() {
    val itens = RegrasConquistas.todas.mapIndexed { indice, regra ->
        ConquistaItem(
            regra = regra,
            progresso = if (indice < 3) regra.meta else regra.meta / 2,
            desbloqueadaEm = if (indice < 3) System.currentTimeMillis() else null
        )
    }
    MaterialTheme {
        ConteudoConquistas(
            uiState = ConquistasUiState(listaDeConquistas = itens, totalDesbloqueadas = 3),
            onFiltroChange = {},
            onPedirReset = {},
            onCancelarReset = {},
            onConfirmarReset = {},
            onErroVisto = {}
        )
    }
}
