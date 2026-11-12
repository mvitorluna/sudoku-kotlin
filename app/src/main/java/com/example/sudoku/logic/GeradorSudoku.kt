package com.example.sudoku.logic

import kotlin.random.Random

object GeradorSudoku {

    fun gerar(nivel: Nivel, random: Random = Random.Default): Sudoku {
        val gabarito = montarGabarito(nivel, random)
        val tabuleiro = esvaziarCasas(gabarito, nivel, random)
        return Sudoku(nivel = nivel, tabuleiro = tabuleiro, gabarito = gabarito)
    }

    private fun montarGabarito(nivel: Nivel, random: Random): List<List<Int>> {
        val tamanho = nivel.tamanho
        val linhasBloco = nivel.linhasBloco
        val colunasBloco = nivel.colunasBloco

        val ordemDasLinhas = mutableListOf<Int>()
        for (faixa in (0 until tamanho / linhasBloco).shuffled(random)) {
            for (linha in (0 until linhasBloco).shuffled(random)) {
                ordemDasLinhas.add(faixa * linhasBloco + linha)
            }
        }

        val ordemDasColunas = mutableListOf<Int>()
        for (pilha in (0 until tamanho / colunasBloco).shuffled(random)) {
            for (coluna in (0 until colunasBloco).shuffled(random)) {
                ordemDasColunas.add(pilha * colunasBloco + coluna)
            }
        }

        val numeros = (1..tamanho).shuffled(random)

        return ordemDasLinhas.map { linha ->
            ordemDasColunas.map { coluna ->
                val posicao = (colunasBloco * (linha % linhasBloco) + linha / linhasBloco + coluna) % tamanho
                numeros[posicao]
            }
        }
    }

    private fun esvaziarCasas(gabarito: List<List<Int>>, nivel: Nivel, random: Random): List<List<Int>> {
        val tabuleiro = gabarito.map { linha -> linha.toMutableList() }
        var casasEsvaziadas = 0

        while (casasEsvaziadas < nivel.casasVazias) {
            val linha = random.nextInt(nivel.tamanho)
            val coluna = random.nextInt(nivel.tamanho)
            if (tabuleiro[linha][coluna] != 0) {
                tabuleiro[linha][coluna] = 0
                casasEsvaziadas++
            }
        }

        return tabuleiro
    }
}
