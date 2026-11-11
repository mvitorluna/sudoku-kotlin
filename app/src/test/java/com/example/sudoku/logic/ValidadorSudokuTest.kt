package com.example.sudoku.logic

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ValidadorSudokuTest {

    @Test
    fun testarGabaritoCompletoVenceu() {
        val sudoku = GeradorSudoku.gerar(Nivel.FACIL, Random(1))
        val gabarito = sudoku.gabarito
        assertTrue(ValidadorSudoku.estaCompleto(gabarito))
        assertTrue(ValidadorSudoku.conflitos(gabarito, Nivel.FACIL).isEmpty())
        assertTrue(ValidadorSudoku.venceu(gabarito, Nivel.FACIL))
    }

    @Test
    fun testarTabuleiroIncompletoNaoEstaCompleto() {
        val sudoku = GeradorSudoku.gerar(Nivel.FACIL, Random(1))
        assertFalse(ValidadorSudoku.estaCompleto(sudoku.tabuleiro))
        assertFalse(ValidadorSudoku.venceu(sudoku.tabuleiro, Nivel.FACIL))
    }

    @Test
    fun testarConflitoLinha() {
        val tabuleiro = listOf(
            listOf(1, 1, 3, 4),
            listOf(3, 4, 1, 2),
            listOf(2, 3, 4, 1),
            listOf(4, 2, 1, 3)
        )
        assertTrue(ValidadorSudoku.temConflito(tabuleiro, 0, 0, Nivel.FACIL))
        assertTrue(ValidadorSudoku.temConflito(tabuleiro, 0, 1, Nivel.FACIL))
        val conflitos = ValidadorSudoku.conflitos(tabuleiro, Nivel.FACIL)
        assertTrue(conflitos.contains(0 to 0))
        assertTrue(conflitos.contains(0 to 1))
    }

    @Test
    fun testarConflitoColuna() {
        val tabuleiro = listOf(
            listOf(1, 2, 3, 4),
            listOf(1, 4, 2, 3),
            listOf(2, 3, 4, 1),
            listOf(4, 1, 1, 2)
        )
        assertTrue(ValidadorSudoku.temConflito(tabuleiro, 0, 0, Nivel.FACIL))
        assertTrue(ValidadorSudoku.temConflito(tabuleiro, 1, 0, Nivel.FACIL))
        val conflitos = ValidadorSudoku.conflitos(tabuleiro, Nivel.FACIL)
        assertTrue(conflitos.contains(0 to 0))
        assertTrue(conflitos.contains(1 to 0))
    }

    @Test
    fun testarConflitoBloco() {
        val tabuleiro = listOf(
            listOf(1, 2, 3, 4),
            listOf(3, 1, 4, 2),
            listOf(2, 4, 1, 3),
            listOf(4, 3, 2, 1)
        )
        assertTrue(ValidadorSudoku.temConflito(tabuleiro, 0, 0, Nivel.FACIL))
        assertTrue(ValidadorSudoku.temConflito(tabuleiro, 1, 1, Nivel.FACIL))
        val conflitos = ValidadorSudoku.conflitos(tabuleiro, Nivel.FACIL)
        assertTrue(conflitos.contains(0 to 0))
        assertTrue(conflitos.contains(1 to 1))
    }
}
