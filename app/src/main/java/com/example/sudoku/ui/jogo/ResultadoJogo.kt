package com.example.sudoku.ui.jogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sudoku.logic.Nivel
import com.example.sudoku.logic.RegraConquista
import com.example.sudoku.logic.RegrasConquistas

@Composable
fun ResultadoJogo(
    uiState: JogoUiState,
    onNomeChange: (String) -> Unit,
    onPublicar: () -> Unit,
    onJogarDeNovo: () -> Unit,
    onVoltar: () -> Unit
) {
    val venceu = uiState.status == StatusJogo.VENCEU

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (venceu) "Você venceu!" else "Fim de jogo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (venceu) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Text(
            text = if (venceu) uiState.titulo else "Você errou ${JogoViewModel.MAXIMO_DE_ERROS} vezes",
            style = MaterialTheme.typography.bodyLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LinhaResultado("Tempo", uiState.tempoFormatado)
                LinhaResultado("Erros", uiState.erros.toString())
                LinhaResultado("Dicas usadas", uiState.dicasUsadas.toString())

                if (venceu) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LinhaResultado("Base do nível", "${uiState.nivel.pontosBase}")
                    LinhaResultado("Tempo (1 por segundo)", "- ${uiState.segundos}")
                    LinhaResultado("Erros (50 cada)", "- ${50 * uiState.erros}")
                    LinhaResultado("Dicas (100 cada)", "- ${100 * uiState.dicasUsadas}")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                LinhaResultado("Pontos", uiState.pontos.toString(), destaque = true)
            }
        }

        if (uiState.conquistasNovas.isNotEmpty()) {
            Text("Conquistas desbloqueadas", style = MaterialTheme.typography.titleMedium)
            uiState.conquistasNovas.forEach { regra ->
                UmaConquistaNova(regra)
            }
        }

        if (venceu) {
            if (uiState.publicado) {
                Text(
                    text = "Resultado publicado no ranking!",
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                TextField(
                    value = uiState.nomeParaRanking,
                    onValueChange = onNomeChange,
                    label = { Text("Seu nome para o ranking") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = onPublicar,
                    enabled = uiState.podePublicar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publicar no ranking")
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onVoltar, modifier = Modifier.weight(1f)) {
                Text("Voltar")
            }
            Button(onClick = onJogarDeNovo, modifier = Modifier.weight(1f)) {
                Text("Jogar de novo")
            }
        }
    }
}

@Composable
fun LinhaResultado(rotulo: String, valor: String, destaque: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(rotulo, fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal)
        Text(valor, fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun UmaConquistaNova(regra: RegraConquista) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = regra.titulo,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(regra.titulo, fontWeight = FontWeight.Bold)
                Text(regra.descricao, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResultadoJogoPreview() {
    MaterialTheme {
        ResultadoJogo(
            uiState = JogoUiState(
                nivel = Nivel.FACIL,
                status = StatusJogo.VENCEU,
                segundos = 95,
                erros = 1,
                dicasRestantes = 2,
                pontos = 755,
                conquistasNovas = RegrasConquistas.todas.take(2)
            ),
            onNomeChange = {},
            onPublicar = {},
            onJogarDeNovo = {},
            onVoltar = {}
        )
    }
}
