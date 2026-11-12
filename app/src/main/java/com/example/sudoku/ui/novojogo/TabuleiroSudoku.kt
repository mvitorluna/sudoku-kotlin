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
import androidx.compose.ui.unit.TextUnit
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
    val quantidadeDeFaixas = nivel.tamanho / nivel.linhasBloco
    val blocosPorFaixa = nivel.tamanho / nivel.colunasBloco
    val corBorda = MaterialTheme.colorScheme.onSurface

    val tamanhoFonte = when (nivel) {
        Nivel.FACIL -> 24.sp
        Nivel.MEDIO -> 20.sp
        Nivel.DIFICIL -> 16.sp
    }

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

                                    CasaSudoku(
                                        valor = tabuleiro[linha][coluna],
                                        fixa = casa in fixas,
                                        comConflito = casa in conflitos,
                                        selecionada = casa == selecionada,
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
    tamanhoFonte: TextUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val corFundo = if (selecionada) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val corTexto = when {
        comConflito -> MaterialTheme.colorScheme.error
        fixa -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .background(corFundo)
            .border(0.5.dp, MaterialTheme.colorScheme.outline)
            .clickable { onClick() }
    ) {
        if (valor != 0) {
            Text(
                text = valor.toString(),
                fontSize = tamanhoFonte,
                fontWeight = if (fixa) FontWeight.Bold else FontWeight.Normal,
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

@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuFacilPreview() {
    TabuleiroDeExemplo(Nivel.FACIL)
}

@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuMedioPreview() {
    TabuleiroDeExemplo(Nivel.MEDIO)
}

@Preview(showBackground = true)
@Composable
fun TabuleiroSudokuDificilPreview() {
    TabuleiroDeExemplo(Nivel.DIFICIL)
}
