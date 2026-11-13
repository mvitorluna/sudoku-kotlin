package com.example.sudoku.ui.jogo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.sin
import kotlin.random.Random
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

    // Ícone entra com uma mola (quica). Na derrota ele também balança de lado.
    val entrada = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entrada.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    // Pontos sobem de 0 até o total
    var pontosAlvo by remember { mutableIntStateOf(0) }
    LaunchedEffect(uiState.pontos) { pontosAlvo = uiState.pontos }
    val pontosAnimados by animateIntAsState(pontosAlvo, tween(1200), label = "pontos")

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (venceu) Icons.Default.EmojiEvents else Icons.Default.SentimentVeryDissatisfied,
                contentDescription = null,
                tint = if (venceu) Color(0xFFFFB300) else MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer {
                        scaleX = entrada.value
                        scaleY = entrada.value
                        rotationZ = if (venceu) 0f else (1f - entrada.value) * 40f
                    }
            )
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
                    LinhaResultado("Pontos", pontosAnimados.toString(), destaque = true)
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

        // Confete por cima de tudo, só na vitória
        if (venceu) Confetes(Modifier.matchParentSize())
    }
}

private data class Confete(
    val x: Float,
    val atraso: Float,
    val velocidade: Float,
    val balanco: Float,
    val giro: Float,
    val cor: Color
)

// Pedacinhos coloridos caindo e girando, desenhados num Canvas. Toca uma vez só (3,5 s).
@Composable
private fun Confetes(modifier: Modifier = Modifier) {
    val cores = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        Color(0xFFFFB300),
        Color(0xFFE91E63)
    )
    val confetes = remember {
        List(70) {
            Confete(
                x = Random.nextFloat(),
                atraso = Random.nextFloat() * 0.4f,
                velocidade = 0.7f + Random.nextFloat() * 0.6f,
                balanco = 10f + Random.nextFloat() * 30f,
                giro = Random.nextFloat() * 360f,
                cor = cores.random()
            )
        }
    }
    val tempo = remember { Animatable(0f) }
    LaunchedEffect(Unit) { tempo.animateTo(1f, tween(3500, easing = LinearEasing)) }

    Canvas(modifier = modifier) {
        confetes.forEach { c ->
            // t vai de 0 (topo) a 1 (fundo); cada confete começa num momento diferente
            val t = (tempo.value - c.atraso) * c.velocidade / 0.5f
            if (t in 0f..1f) {
                val centro = Offset(
                    x = c.x * size.width + sin(t * 12f + c.giro) * c.balanco,
                    y = t * size.height
                )
                rotate(degrees = c.giro + t * 720f, pivot = centro) {
                    drawRect(
                        color = c.cor,
                        topLeft = centro - Offset(10f, 5f),
                        size = Size(20f, 10f),
                        alpha = 1f - t * 0.6f
                    )
                }
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
