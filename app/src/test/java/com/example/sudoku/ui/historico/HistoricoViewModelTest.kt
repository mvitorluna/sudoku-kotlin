package com.example.sudoku.ui.historico

import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.local.PartidaDAO
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.logic.Nivel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoricoViewModelTest {
    private val partidas = listOf(
        Partida(id = 1, nivel = "FACIL", venceu = true, tempoSegundos = 50, erros = 0, dicasUsadas = 0, pontos = 950, data = 1),
        Partida(id = 2, nivel = "MEDIO", venceu = false, tempoSegundos = 300, erros = 3, dicasUsadas = 0, pontos = 0, data = 2),
        Partida(id = 3, nivel = "FACIL", venceu = true, tempoSegundos = 30, erros = 1, dicasUsadas = 1, pontos = 820, data = 3, favorita = true),
        Partida(id = 4, nivel = "DIFICIL", venceu = true, tempoSegundos = 900, erros = 0, dicasUsadas = 0, pontos = 2100, data = 4, semente = 77),
        Partida(id = 5, nivel = "FACIL", venceu = true, tempoSegundos = 40, erros = 0, dicasUsadas = 0, pontos = 960, data = 5)
    )

    private lateinit var dao: PartidaDAOFalso

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dao = PartidaDAOFalso(partidas)
    }

    @After
    fun limpar() {
        Dispatchers.resetMain()
    }

    private fun ids(viewModel: HistoricoViewModel) = viewModel.uiState.value.listaDePartidas.map { it.id }

    @Test
    fun comecaOrdenadoPelaMaisRecente() {
        assertEquals(listOf(5, 4, 3, 2, 1), ids(HistoricoViewModel(PartidaRepository(dao))))
    }

    @Test
    fun filtrosSeSomam() {
        val viewModel = HistoricoViewModel(PartidaRepository(dao))
        viewModel.onFiltroNivelChange(Nivel.FACIL)
        assertEquals(listOf(5, 3, 1), ids(viewModel))
        viewModel.onSoFavoritasChange()
        assertEquals(listOf(3), ids(viewModel))
        viewModel.onSoFavoritasChange()
        viewModel.onFiltroNivelChange(null)
        viewModel.onSoDesafiosChange()
        assertEquals(listOf(4), ids(viewModel))
    }

    @Test
    fun soVitoriasTiraDerrotas() {
        val viewModel = HistoricoViewModel(PartidaRepository(dao))
        viewModel.onSoVitoriasChange()
        assertEquals(listOf(5, 4, 3, 1), ids(viewModel))
    }

    @Test
    fun ordenacoes() {
        val viewModel = HistoricoViewModel(PartidaRepository(dao))
        viewModel.onOrdenacaoChange(OrdenacaoPartidas.MENOR_TEMPO)
        assertEquals(listOf(3, 5, 1, 2, 4), ids(viewModel))
        viewModel.onOrdenacaoChange(OrdenacaoPartidas.MAIS_PONTOS)
        assertEquals(listOf(4, 5, 1, 3, 2), ids(viewModel))
    }

    @Test
    fun favoritarEExcluirAtualizamAListaPeloFlow() {
        val viewModel = HistoricoViewModel(PartidaRepository(dao))
        viewModel.onFavoritar(partidas[0])
        assertTrue(viewModel.uiState.value.listaDePartidas.first { it.id == 1 }.favorita)
        viewModel.onDelete(partidas[1])
        assertEquals(listOf(5, 4, 3, 1), ids(viewModel))
    }

    @Test
    fun limparHistoricoSoDepoisDeConfirmar() {
        val viewModel = HistoricoViewModel(PartidaRepository(dao))
        viewModel.onPedirLimpar()
        assertTrue(viewModel.uiState.value.confirmandoLimpar)
        viewModel.onCancelarLimpar()
        assertEquals(5, ids(viewModel).size)
        viewModel.onPedirLimpar()
        viewModel.onConfirmarLimpar()
        assertEquals(emptyList<Int>(), ids(viewModel))
        assertEquals("Você ainda não jogou nenhuma partida", viewModel.uiState.value.textoListaVazia)
    }

    @Test
    fun expandirETocarDeNovoFecha() {
        val viewModel = HistoricoViewModel(PartidaRepository(dao))
        viewModel.onPartidaClick(partidas[0])
        assertEquals(1, viewModel.uiState.value.partidaExpandidaId)
        viewModel.onPartidaClick(partidas[0])
        assertNull(viewModel.uiState.value.partidaExpandidaId)
    }

    @Test
    fun erroDoBancoVaiParaOUiState() {
        val viewModel = HistoricoViewModel(PartidaRepository(PartidaDAOFalso(emptyList(), falhar = true)))
        assertNotNull(viewModel.uiState.value.erro)
    }

    @Test
    fun estatisticasCalculadasDaLista() {
        val estado = EstatisticasViewModel(PartidaRepository(dao)).uiState.value
        assertEquals(5, estado.totalPartidas)
        assertEquals(4, estado.vitorias)
        assertEquals(80, estado.percentualVitorias)
        assertEquals(3, estado.sequenciaAtual)

        val facil = estado.porNivel.first { it.nivel == Nivel.FACIL }
        assertEquals(3, facil.jogadas)
        assertEquals(30, facil.melhorTempo)
        assertEquals((950 + 820 + 960) / 3.0, facil.mediaPontos!!, 0.001)

        val medio = estado.porNivel.first { it.nivel == Nivel.MEDIO }
        assertEquals(1, medio.jogadas)
        assertNull(medio.melhorTempo)
        assertEquals("-", medio.textoMediaPontos)
    }

    @Test
    fun estatisticasSemPartidas() {
        val estado = EstatisticasViewModel(PartidaRepository(PartidaDAOFalso(emptyList()))).uiState.value
        assertEquals(0, estado.percentualVitorias)
        assertEquals(0, estado.sequenciaAtual)
    }
}

private class PartidaDAOFalso(iniciais: List<Partida>, private val falhar: Boolean = false) : PartidaDAO {
    private val fluxo = MutableStateFlow(iniciais)

    override suspend fun inserir(partida: Partida) {
        fluxo.value = fluxo.value + partida
    }

    override fun buscarTodos(): Flow<List<Partida>> =
        if (falhar) flow { throw IllegalStateException("banco quebrado") } else fluxo

    override suspend fun buscarTodosAgora(): List<Partida> = fluxo.value

    override suspend fun atualizar(partida: Partida) {
        fluxo.value = fluxo.value.map { if (it.id == partida.id) partida else it }
    }

    override suspend fun deletar(partida: Partida) {
        fluxo.value = fluxo.value.filter { it.id != partida.id }
    }

    override suspend fun deletarTodos() {
        fluxo.value = emptyList()
    }
}
