package com.example.sudoku.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class PontuacaoTest {

    @Test
    fun testarCalculoPontuacaoNormal() {
        val pontos = Pontuacao.calcular(Nivel.FACIL, 60, 1, 1)
        assertEquals(790, pontos)
    }

    @Test
    fun testarCalculoPontuacaoMinimoZero() {
        val pontos = Pontuacao.calcular(Nivel.FACIL, 2000, 10, 10)
        assertEquals(0, pontos)
    }

    @Test
    fun testarPontuacaoMedioEDificil() {
        val pontosMedio = Pontuacao.calcular(Nivel.MEDIO, 100, 2, 1)
        assertEquals(1700, pontosMedio)

        val pontosDificil = Pontuacao.calcular(Nivel.DIFICIL, 200, 0, 0)
        assertEquals(2800, pontosDificil)
    }
}
