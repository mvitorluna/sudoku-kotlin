package com.example.sudoku.logic

import kotlin.random.Random

object GeradorSudoku {

    fun gerar(nivel: Nivel, random: Random = Random.Default): Sudoku {
        val n = nivel.tamanho
        val bl = nivel.linhasBloco
        val bc = nivel.colunasBloco

        val bandas = (0 until bc).shuffled(random)
        val linhas = bandas.flatMap { b ->
            (0 until bl).shuffled(random).map { l -> b * bl + l }
        }

        val pilhas = (0 until bl).shuffled(random)
        val colunas = pilhas.flatMap { p ->
            (0 until bc).shuffled(random).map { c -> p * bc + c }
        }

        val nums = (1..n).shuffled(random)

        val gabarito = linhas.map { l ->
            colunas.map { c ->
                val pos = (bc * (l % bl) + l / bl + c) % n
                nums[pos]
            }
        }

        val todasPosicoes = (0 until n).flatMap { l ->
            (0 until n).map { c -> l to c }
        }
        val casasParaEsvaziar = todasPosicoes.shuffled(random).take(nivel.casasVazias).toSet()

        val tabuleiro = gabarito.mapIndexed { l, linha ->
            linha.mapIndexed { c, valor ->
                if (l to c in casasParaEsvaziar) 0 else valor
            }
        }

        return Sudoku(
            nivel = nivel,
            tabuleiro = tabuleiro,
            gabarito = gabarito
        )
    }
}
