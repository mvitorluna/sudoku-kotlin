package com.example.sudoku.ui.conquistas

import com.example.sudoku.data.local.Conquista
import com.example.sudoku.data.local.ConquistaDAO
import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.local.PartidaDAO
import com.example.sudoku.data.repository.ConquistaRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class PartidaDaoFalso(inicial: List<Partida>) : PartidaDAO {
    val partidas = MutableStateFlow(inicial)
    override suspend fun inserir(partida: Partida) { partidas.value = partidas.value + partida }
    override fun buscarTodos(): Flow<List<Partida>> = partidas
    override suspend fun buscarTodosAgora(): List<Partida> = partidas.value
    override suspend fun atualizar(partida: Partida) {}
    override suspend fun deletar(partida: Partida) {}
    override suspend fun deletarTodos() { partidas.value = emptyList() }
}

private class ConquistaDaoFalso(inicial: List<Conquista>, val falhar: Boolean = false) : ConquistaDAO {
    val conquistas = MutableStateFlow(inicial)
    override suspend fun inserir(conquista: Conquista) { conquistas.value = conquistas.value + conquista }
    override fun buscarTodos(): Flow<List<Conquista>> =
        if (falhar) flow { throw IllegalStateException("banco") } else conquistas
    override suspend fun buscarTodosAgora(): List<Conquista> = conquistas.value
    override suspend fun atualizar(conquista: Conquista) {
        conquistas.value = conquistas.value.map { if (it.codigo == conquista.codigo) conquista else it }
    }
    override suspend fun deletarTodos() { conquistas.value = emptyList() }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ConquistasViewModelTest {
    @Before
    fun preparar() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun limpar() = Dispatchers.resetMain()

    private fun partida(venceu: Boolean, data: Long) = Partida(
        nivel = "FACIL", venceu = venceu, tempoSegundos = 100, erros = 1,
        dicasUsadas = 1, pontos = 500, data = data
    )

    private fun criar(partidas: List<Partida>, conquistas: List<Conquista>, falhar: Boolean = false) =
        ConquistasViewModel(ConquistaRepository(ConquistaDaoFalso(conquistas, falhar), PartidaDaoFalso(partidas)))

    @Test
    fun desbloqueadasVemPrimeiroEDepoisAsMaisPertoDeCompletar() {
        val partidas = List(5) { partida(venceu = true, data = it.toLong()) }
        val vm = criar(partidas, listOf(Conquista("PRIMEIRA_VITORIA", desbloqueadaEm = 10)))
        val lista = vm.uiState.value.listaDeConquistas
        assertEquals(10, lista.size)
        assertEquals("PRIMEIRA_VITORIA", lista.first().regra.codigo)
        assertTrue(lista.first().desbloqueada)
        val bloqueadas = lista.drop(1)
        assertTrue(bloqueadas.none { it.desbloqueada })
        assertEquals(bloqueadas.map { it.percentual }.sortedDescending(), bloqueadas.map { it.percentual })
        assertEquals(1, vm.uiState.value.totalDesbloqueadas)
        assertEquals("1 de 10", vm.uiState.value.textoContador)
    }

    @Test
    fun filtrosSeparamDesbloqueadasEBloqueadas() {
        val vm = criar(emptyList(), listOf(Conquista("MARATONA", desbloqueadaEm = 10), Conquista("PERFEITO", desbloqueadaEm = 20)))
        vm.onFiltroChange(FiltroConquista.DESBLOQUEADAS)
        assertEquals(setOf("MARATONA", "PERFEITO"), vm.uiState.value.listaDeConquistas.map { it.regra.codigo }.toSet())
        vm.onFiltroChange(FiltroConquista.BLOQUEADAS)
        assertEquals(8, vm.uiState.value.listaDeConquistas.size)
        assertEquals(2, vm.uiState.value.totalDesbloqueadas)
        vm.onFiltroChange(FiltroConquista.TODAS)
        assertEquals(10, vm.uiState.value.listaDeConquistas.size)
    }

    @Test
    fun listaAtualizaQuandoUmaConquistaNovaESalva() {
        val conquistaDao = ConquistaDaoFalso(emptyList())
        val vm = ConquistasViewModel(ConquistaRepository(conquistaDao, PartidaDaoFalso(emptyList())))
        assertEquals(0, vm.uiState.value.totalDesbloqueadas)
        assertEquals(false, vm.uiState.value.temNovaNaoVista)
        conquistaDao.conquistas.value = listOf(Conquista("SEM_AJUDA", desbloqueadaEm = 5))
        assertEquals(1, vm.uiState.value.totalDesbloqueadas)
        assertEquals(true, vm.uiState.value.temNovaNaoVista)
        vm.onTelaAberta()
        assertEquals(false, vm.uiState.value.temNovaNaoVista)
    }

    @Test
    fun erroDoBancoVaiParaOUiState() {
        val vm = criar(emptyList(), emptyList(), falhar = true)
        assertEquals("Não foi possível carregar as conquistas", vm.uiState.value.erro)
        vm.onErroVisto()
        assertEquals(null, vm.uiState.value.erro)
    }

    @Test
    fun textoDaDataFicaPronto() {
        val item = ConquistaItem(com.example.sudoku.logic.RegrasConquistas.todas.first(), 1, 0L)
        assertTrue(item.textoData.matches(Regex("\\d{2}/\\d{2}/\\d{4}")))
        assertEquals("", item.copy(desbloqueadaEm = null).textoData)
    }
}
