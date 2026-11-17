package com.example.sudoku.ui.desafios

// ╔══════════════════════════════════════════════╗
// ║  AbaDesafios.kt                              ║
// ║  ABA Desafios (jogador)                      ║
// ║  Lista de desafios ativos para jogar         ║
// ╚══════════════════════════════════════════════╝

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.data.local.AppDatabase
import com.example.sudoku.data.local.DesafioEntity
import com.example.sudoku.data.repository.DesafioRepository

@Composable
fun AbaDesafios(
    onAbrirDesafio: (String) -> Unit,
    viewModel: DesafiosViewModel = viewModel(
        factory = DesafiosViewModelFactory(
            DesafioRepository(AppDatabase.getDatabase(LocalContext.current).desafioDAO()),
            apenasAtivos = true
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConteudoDesafios(
        uiState = uiState,
        onAbrirDesafio = onAbrirDesafio,
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@Composable
fun ConteudoDesafios(
    uiState: DesafiosUiState,
    onAbrirDesafio: (String) -> Unit,
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
            text = "Desafios Especiais",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.desafios.isEmpty() && !uiState.carregando) {
            Text(
                text = "Nenhum desafio disponível no momento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.desafios) { desafio ->
                    CardDesafio(
                        desafio = desafio,
                        onClick = { onAbrirDesafio(desafio.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CardDesafio(
    desafio: DesafioEntity,
    onClick: () -> Unit
) {
    val borderColor = if (desafio.concluido) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outlineVariant
    val border = BorderStroke(if (desafio.concluido) 2.dp else 1.dp, borderColor)

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = border,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = desafio.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (desafio.concluido) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Concluído",
                            tint = Color(0xFF4CAF50)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desafio.descricao,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Jogar Desafio")
                }
            }
        }
    }
}

// ── Preview ──────────────────────────────────
@Preview(showBackground = true)
@Composable
fun ConteudoDesafiosPreview() {
    MaterialTheme {
        ConteudoDesafios(
            uiState = DesafiosUiState(
                desafios = listOf(
                    DesafioEntity("1", "Cronômetro 3 Min", "Teste rápido", "CRONOMETRO", 180, 45, 1, 3, true, 1, true)
                ),
                carregando = false
            ),
            onAbrirDesafio = {},
            onErroVisto = {}
        )
    }
}
