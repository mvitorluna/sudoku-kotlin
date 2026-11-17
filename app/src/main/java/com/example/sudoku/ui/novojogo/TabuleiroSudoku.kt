package com.example.sudoku.ui.novojogo

// ╔══════════════════════════════════════════════╗
// ║  TabuleiroSudoku.kt                          ║
// ║  Componente do tabuleiro                     ║
// ║  Desenha as casas e os blocos                ║
// ╚══════════════════════════════════════════════╝

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.logic.GeradorSudoku
import com.example.sudoku.logic.Nivel
import com.example.sudoku.logic.ValidadorSudoku
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun TabuleiroSudoku(
    nivel: Nivel,
    tabuleiro: List<List<Int>>,
    fixas: Set<Pair<Int, Int>>,
    conflitos: Set<Pair<Int, Int>>,
    selecionada: Pair<Int, Int>?,
    onCasaClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val quantidadeDeFaixas = nivel.tamanho / nivel.linhasBloco
    val blocosPorFaixa = nivel.tamanho / nivel.colunasBloco
    val corBorda = MaterialTheme.colorScheme.onSurface

    val tamanhoFonte = when (nivel) {
        Nivel.FACIL -> 24.sp
        Nivel.MEDIO -> 20.sp
        Nivel.DIFICIL -> 16.sp
    }

    // Número da casa selecionada (0 = casa vazia, não destaca nada)
    val valorSelecionado = selecionada?.let { (l, c) -> tabuleiro[l][c] } ?: 0

    // Casas em linha/coluna/bloco completo. Quando uma casa entra aqui, ela pisca.
    val completas = remember(tabuleiro) { ValidadorSudoku.casasEmGruposCompletos(tabuleiro, nivel) }

    fun mesmoBloco(a: Pair<Int, Int>, b: Pair<Int, Int>) =
        a.first / nivel.linhasBloco == b.first / nivel.linhasBloco &&
            a.second / nivel.colunasBloco == b.second / nivel.colunasBloco

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, corBorda)
    ) {
        for (faixa in 0 until quantidadeDeFaixas) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (bloco in 0 until blocosPorFaixa) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, corBorda)
                    ) {
                        for (linhaNoBloco in 0 until nivel.linhasBloco) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                for (colunaNoBloco in 0 until nivel.colunasBloco) {
                                    val linha = faixa * nivel.linhasBloco + linhaNoBloco
                                    val coluna = bloco * nivel.colunasBloco + colunaNoBloco
                                    val casa = linha to coluna

                                    val valor = tabuleiro[linha][coluna]

                                    CasaSudoku(
                                        valor = valor,
                                        fixa = casa in fixas,
                                        comConflito = casa in conflitos,
                                        selecionada = casa == selecionada,
                                        mesmoNumero = valorSelecionado != 0 && valor == valorSelecionado,
                                        relacionada = selecionada != null && (
                                            linha == selecionada.first ||
                                                coluna == selecionada.second ||
                                                mesmoBloco(casa, selecionada)
                                            ),
                                        completa = casa in completas,
                                        // Atraso pela distância do canto: a piscada anda em onda
                                        atrasoMs = (linha + coluna) * 35,
                                        tamanhoFonte = tamanhoFonte,
                                        onClick = { onCasaClick(linha, coluna) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CasaSudoku(
    valor: Int,
    fixa: Boolean,
    comConflito: Boolean,
    selecionada: Boolean,
    mesmoNumero: Boolean,
    relacionada: Boolean,
    completa: Boolean,
    atrasoMs: Int,
    tamanhoFonte: TextUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val corFundo = when {
        selecionada -> MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
        comConflito -> MaterialTheme.colorScheme.errorContainer
        mesmoNumero -> MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        relacionada -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    // Piscada quando a casa passa a fazer parte de um grupo completo.
    // Começa com o valor atual para não piscar ao abrir o jogo ou girar a tela.
    val brilho = remember { Animatable(0f) }
    var completaAntes by remember { mutableStateOf(completa) }
    LaunchedEffect(completa) {
        val acabouDeCompletar = completa && !completaAntes
        completaAntes = completa
        if (acabouDeCompletar) {
            delay(atrasoMs.toLong())
            brilho.animateTo(1f, tween(150))
            brilho.animateTo(0f, tween(450))
        }
    }
    // Verde = acerto (o tertiary do tema é avermelhado e parecia erro)
    val corBrilho = Color(0xFF4CAF50)

    val corTexto = when {
        comConflito -> MaterialTheme.colorScheme.error
        fixa -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = 1f + 0.12f * brilho.value
                scaleY = 1f + 0.12f * brilho.value
            }
            .background(corFundo)
            .background(corBrilho.copy(alpha = 0.6f * brilho.value))
            .border(0.5.dp, MaterialTheme.colorScheme.outline)
            .clickable { onClick() }
    ) {
        if (valor != 0) {
            Text(
                text = valor.toString(),
                fontSize = tamanhoFonte,
                fontWeight = if (fixa || mesmoNumero) FontWeight.Bold else FontWeight.Normal,
                color = corTexto
            )
        }
    }
}

private fun casasFixas(tabuleiro: List<List<Int>>): Set<Pair<Int, Int>> {
    val fixas = mutableSetOf<Pair<Int, Int>>()
    for (linha in tabuleiro.indices) {
        for (coluna in tabuleiro[linha].indices) {
            if (tabuleiro[linha][coluna] != 0) fixas.add(linha to coluna)
        }
    }
    return fixas
}

@Composable
private fun TabuleiroDeExemplo(nivel: Nivel) {
    val jogo = GeradorSudoku.gerar(nivel, Random(1))
    TabuleiroSudoku(
        nivel = nivel,
        tabuleiro = jogo.tabuleiro,
        fixas = casasFixas(jogo.tabuleiro),
        conflitos = emptySet(),
        selecionada = 0 to 0,
        onCasaClick = { _, _ -> },
        modifier = Modifier.padding(16.dp)
    )
}

// ── Previews ─────────────────────────────────
@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuFacilPreview() {
    MaterialTheme {
        TabuleiroDeExemplo(Nivel.FACIL)
    }
}

@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuMedioPreview() {
    MaterialTheme {
        TabuleiroDeExemplo(Nivel.MEDIO)
    }
}

@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuDificilPreview() {
    MaterialTheme {
        TabuleiroDeExemplo(Nivel.DIFICIL)
    }
}
