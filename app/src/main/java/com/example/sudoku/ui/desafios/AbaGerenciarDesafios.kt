package com.example.sudoku.ui.desafios

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun AbaGerenciarDesafios(
    viewModel: DesafiosViewModel = viewModel(
        factory = DesafiosViewModelFactory(
            DesafioRepository(AppDatabase.getDatabase(LocalContext.current).desafioDAO()),
            apenasAtivos = false
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConteudoGerenciarDesafios(
        uiState = uiState,
        onAlternarAtivo = { viewModel.alternarAtivo(it) },
        onAtualizarDesafio = { viewModel.atualizarDesafio(it) },
        onErroVisto = { viewModel.onErroVisto() }
    )
}

@Composable
fun ConteudoGerenciarDesafios(
    uiState: DesafiosUiState,
    onAlternarAtivo: (DesafioEntity) -> Unit,
    onAtualizarDesafio: (DesafioEntity) -> Unit,
    onErroVisto: () -> Unit
) {
    var desafioEmEdicao by remember { mutableStateOf<DesafioEntity?>(null) }

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
            text = "Gerenciar Desafios",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ative ou desative o que aparece para os jogadores, e edite os detalhes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.desafios) { desafio ->
                CardGerenciarDesafio(
                    desafio = desafio,
                    onAlternarAtivo = { onAlternarAtivo(desafio) },
                    onEditar = { desafioEmEdicao = desafio }
                )
            }
        }
    }

    if (desafioEmEdicao != null) {
        DialogEditarDesafio(
            desafio = desafioEmEdicao!!,
            onDismiss = { desafioEmEdicao = null },
            onSalvar = { atualizado ->
                onAtualizarDesafio(atualizado)
                desafioEmEdicao = null
            }
        )
    }
}

@Composable
fun CardGerenciarDesafio(
    desafio: DesafioEntity,
    onAlternarAtivo: () -> Unit,
    onEditar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Desafio")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desafio.descricao,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (desafio.ativo) "Visível para jogadores" else "Oculto para jogadores",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (desafio.ativo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Switch(
                    checked = desafio.ativo,
                    onCheckedChange = { onAlternarAtivo() }
                )
            }
        }
    }
}

@Composable
fun DialogEditarDesafio(
    desafio: DesafioEntity,
    onDismiss: () -> Unit,
    onSalvar: (DesafioEntity) -> Unit
) {
    var titulo by remember { mutableStateOf(desafio.titulo) }
    var descricao by remember { mutableStateOf(desafio.descricao) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Desafio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSalvar(desafio.copy(titulo = titulo, descricao = descricao))
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ConteudoGerenciarDesafiosPreview() {
    MaterialTheme {
        ConteudoGerenciarDesafios(
            uiState = DesafiosUiState(
                desafios = listOf(
                    DesafioEntity("1", "Cronômetro 3 Min", "Teste rápido", "CRONOMETRO", 180, 45, 1, 3, true, 1, false),
                    DesafioEntity("2", "Sem Dicas", "Sem nenhuma dica", "SEM_DICAS", 0, 45, 0, 3, false, 2, false)
                ),
                carregando = false
            ),
            onAlternarAtivo = {},
            onAtualizarDesafio = {},
            onErroVisto = {}
        )
    }
}
