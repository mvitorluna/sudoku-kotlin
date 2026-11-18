package com.example.sudoku.logic

import com.example.sudoku.data.local.Partida
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegrasConquistasTest {
    private fun partida(
        venceu: Boolean = true,
        nivel: String = "FACIL",
        tempoSegundos: Int = 120,
        erros: Int = 1,
        dicasUsadas: Int = 1,
        pontos: Int = 500,
        semente: Long = 0,
        data: Long = 0
    ) = Partida(
        nivel = nivel,
        venceu = venceu,
        tempoSegundos = tempoSegundos,
        erros = erros,
        dicasUsadas = dicasUsadas,
        pontos = pontos,
        semente = semente,
        data = data
    )

    private fun regra(codigo: String) = RegrasConquistas.buscarPorCodigo(codigo)!!

    @Test
    fun existemDezConquistasComCodigosDiferentes() {
        assertEquals(10, RegrasConquistas.todas.size)
        assertEquals(10, RegrasConquistas.todas.map { it.codigo }.distinct().size)
    }

    @Test
    fun semPartidasNenhumaConquistaEhAlcancada() {
        assertTrue(RegrasConquistas.novasConquistas(emptyList(), emptySet()).isEmpty())
    }

    @Test
    fun primeiraVitoriaSoContaVitorias() {
        assertFalse(regra("PRIMEIRA_VITORIA").foiAlcancada(listOf(partida(venceu = false))))
        assertTrue(regra("PRIMEIRA_VITORIA").foiAlcancada(listOf(partida(venceu = true))))
    }

    @Test
    fun perfeitoPrecisaVencerSemErros() {
        assertFalse(regra("PERFEITO").foiAlcancada(listOf(partida(erros = 1))))
        assertFalse(regra("PERFEITO").foiAlcancada(listOf(partida(venceu = false, erros = 0))))
        assertTrue(regra("PERFEITO").foiAlcancada(listOf(partida(erros = 0))))
    }

    @Test
    fun semAjudaPrecisaVencerSemDicas() {
        assertFalse(regra("SEM_AJUDA").foiAlcancada(listOf(partida(dicasUsadas = 2))))
        assertTrue(regra("SEM_AJUDA").foiAlcancada(listOf(partida(dicasUsadas = 0))))
    }

    @Test
    fun relampagoPrecisaSerFacilEmMenosDe60Segundos() {
        assertFalse(regra("RELAMPAGO").foiAlcancada(listOf(partida(tempoSegundos = 60))))
        assertFalse(regra("RELAMPAGO").foiAlcancada(listOf(partida(nivel = "MEDIO", tempoSegundos = 30))))
        assertTrue(regra("RELAMPAGO").foiAlcancada(listOf(partida(tempoSegundos = 59))))
    }

    @Test
    fun mestre9x9PrecisaVencerNoDificil() {
        assertFalse(regra("MESTRE_9X9").foiAlcancada(listOf(partida(nivel = "MEDIO"))))
        assertTrue(regra("MESTRE_9X9").foiAlcancada(listOf(partida(nivel = "DIFICIL"))))
    }

    @Test
    fun exploradorContaNiveisDiferentesVencidos() {
        val partidas = listOf(
            partida(nivel = "FACIL"),
            partida(nivel = "FACIL"),
            partida(nivel = "MEDIO"),
            partida(nivel = "DIFICIL", venceu = false)
        )
        assertEquals(2, regra("EXPLORADOR").progressoAtual(partidas))
        assertTrue(regra("EXPLORADOR").foiAlcancada(partidas + partida(nivel = "DIFICIL")))
    }

    @Test
    fun maratonaContaTodasAsPartidasELimitaNaMeta() {
        val partidas = List(12) { partida(venceu = it % 2 == 0) }
        assertEquals(10, regra("MARATONA").progressoAtual(partidas))
        assertTrue(regra("MARATONA").foiAlcancada(partidas))
        assertFalse(regra("MARATONA").foiAlcancada(partidas.take(9)))
    }

    @Test
    fun emChamasContaVitoriasSeguidasDasMaisRecentes() {
        val partidas = listOf(
            partida(venceu = true, data = 1),
            partida(venceu = false, data = 2),
            partida(venceu = true, data = 3),
            partida(venceu = true, data = 4)
        )
        assertEquals(2, regra("EM_CHAMAS").progressoAtual(partidas))
        assertTrue(regra("EM_CHAMAS").foiAlcancada(partidas + partida(venceu = true, data = 5)))
        assertEquals(0, regra("EM_CHAMAS").progressoAtual(partidas + partida(venceu = false, data = 5)))
    }

    @Test
    fun desafianteSoContaPartidasComSemente() {
        assertFalse(regra("DESAFIANTE").foiAlcancada(listOf(partida(semente = 0))))
        assertTrue(regra("DESAFIANTE").foiAlcancada(listOf(partida(semente = 48213))))
    }

    @Test
    fun colecionadorSomaOsPontos() {
        val partidas = List(4) { partida(pontos = 2_500) }
        assertTrue(regra("COLECIONADOR").foiAlcancada(partidas))
        assertFalse(regra("COLECIONADOR").foiAlcancada(partidas.drop(1)))
    }

    @Test
    fun novasConquistasIgnoraAsJaDesbloqueadas() {
        val partidas = listOf(partida(erros = 0, dicasUsadas = 0))
        val primeiraVez = RegrasConquistas.novasConquistas(partidas, emptySet()).map { it.codigo }
        assertEquals(setOf("PRIMEIRA_VITORIA", "PERFEITO", "SEM_AJUDA"), primeiraVez.toSet())

        val segundaVez = RegrasConquistas.novasConquistas(partidas, primeiraVez.toSet())
        assertTrue(segundaVez.isEmpty())
    }

    @Test
    fun buscarPorCodigoInexistenteDevolveNulo() {
        assertNull(RegrasConquistas.buscarPorCodigo("NAO_EXISTE"))
    }
}
