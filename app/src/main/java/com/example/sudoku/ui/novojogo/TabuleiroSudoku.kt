package com.example.sudoku.ui.novojogo

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.logic.GeradorSudoku
import com.example.sudoku.logic.Nivel
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
    val n = nivel.tamanho
    val bl = nivel.linhasBloco
    val bc = nivel.colunasBloco

    val tamanhoFonte = when (nivel) {
        Nivel.FACIL -> 24.sp
        Nivel.MEDIO -> 20.sp
        Nivel.DIFICIL -> 16.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.outline)
    ) {
        for (r in 0 until n) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (c in 0 until n) {
                    val ehSelecionada = selecionada == (r to c)
                    val ehFixa = fixas.contains(r to c)
                    val temConflito = conflitos.contains(r to c)
                    val valor = tabuleiro.getOrNull(r)?.getOrNull(c) ?: 0

                    val bordaCima = if (r % bl == 0) 2.dp else 0.5.dp
                    val bordaEsquerda = if (c % bc == 0) 2.dp else 0.5.dp

                    val corFundo = if (ehSelecionada) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                    val corTexto = when {
                        temConflito -> MaterialTheme.colorScheme.error
                        ehFixa -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(corFundo)
                            .border(bordaCima, MaterialTheme.colorScheme.outline)
                            .border(bordaEsquerda, MaterialTheme.colorScheme.outline)
                            .clickable { onCasaClick(r, c) }
                    ) {
                        if (valor != 0) {
                            Text(
                                text = valor.toString(),
                                fontSize = tamanhoFonte,
                                fontWeight = if (ehFixa) FontWeight.Bold else FontWeight.Normal,
                                color = corTexto
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuFacilPreview() {
    val jogo = GeradorSudoku.gerar(Nivel.FACIL, Random(1))
    val fixas = mutableSetOf<Pair<Int, Int>>()
    for (r in 0 until 4) {
        for (c in 0 until 4) {
            if (jogo.tabuleiro[r][c] != 0) fixas.add(r to c)
        }
    }
    TabuleiroSudoku(
        nivel = Nivel.FACIL,
        tabuleiro = jogo.tabuleiro,
        fixas = fixas,
        conflitos = emptySet(),
        selecionada = 0 to 0,
        onCasaClick = { _, _ -> },
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuMedioPreview() {
    val jogo = GeradorSudoku.gerar(Nivel.MEDIO, Random(1))
    val fixas = mutableSetOf<Pair<Int, Int>>()
    for (r in 0 until 6) {
        for (c in 0 until 6) {
            if (jogo.tabuleiro[r][c] != 0) fixas.add(r to c)
        }
    }
    TabuleiroSudoku(
        nivel = Nivel.MEDIO,
        tabuleiro = jogo.tabuleiro,
        fixas = fixas,
        conflitos = emptySet(),
        selecionada = 1 to 1,
        onCasaClick = { _, _ -> },
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuDificilPreview() {
    val jogo = GeradorSudoku.gerar(Nivel.DIFICIL, Random(1))
    val fixas = mutableSetOf<Pair<Int, Int>>()
    for (r in 0 until 9) {
        for (c in 0 until 9) {
            if (jogo.tabuleiro[r][c] != 0) fixas.add(r to c)
        }
    }
    TabuleiroSudoku(
        nivel = Nivel.DIFICIL,
        tabuleiro = jogo.tabuleiro,
        fixas = fixas,
        conflitos = emptySet(),
        selecionada = 2 to 2,
        onCasaClick = { _, _ -> },
        modifier = Modifier.padding(16.dp)
    )
}
