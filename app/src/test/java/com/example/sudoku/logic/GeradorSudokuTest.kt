package com.example.sudoku.logic

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class GeradorSudokuTest {
    @Test
    fun testarGeracaoParaTodosOsNiveis() {
        for (nivel in Nivel.entries) {
            val n = nivel.tamanho
            val bl = nivel.linhasBloco
            val bc = nivel.colunasBloco

            repeat(100) {
                val sudoku = GeradorSudoku.gerar(nivel)
                val gabarito = sudoku.gabarito
                val tabuleiro = sudoku.tabuleiro

                val conjuntoEsperado = (1..n).toSet()

                for (r in 0 until n) {
                    assertEquals(conjuntoEsperado, gabarito[r].toSet())
                }

                for (c in 0 until n) {
                    val col = (0 until n).map { r -> gabarito[r][c] }.toSet()
                    assertEquals(conjuntoEsperado, col)
                }

                for (br in 0 until n step bl) {
                    for (bcIdx in 0 until n step bc) {
                        val bloco = mutableSetOf<Int>()
                        for (r in br until br + bl) {
                            for (c in bcIdx until bcIdx + bc) {
                                bloco.add(gabarito[r][c])
                            }
                        }
                        assertEquals(conjuntoEsperado, bloco)
                    }
                }

                var zeros = 0
                for (r in 0 until n) {
                    for (c in 0 until n) {
                        val valTab = tabuleiro[r][c]
                        val valGab = gabarito[r][c]
                        if (valTab == 0) {
                            zeros++
                        } else {
                            assertEquals(valGab, valTab)
                        }
                    }
                }
                assertEquals(nivel.casasVazias, zeros)
            }
        }
    }

    @Test
    fun testarSementeDeterministica() {
        for (nivel in Nivel.entries) {
            val s1 = GeradorSudoku.gerar(nivel, Random(42))
            val s2 = GeradorSudoku.gerar(nivel, Random(42))
            assertEquals(s1.gabarito, s2.gabarito)
            assertEquals(s1.tabuleiro, s2.tabuleiro)
        }
    }
}
