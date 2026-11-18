package com.example.sudoku.ui.ranking

import com.example.sudoku.data.remote.RankingFirestore
import com.example.sudoku.data.remote.RankingRemoto
import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.logic.Nivel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RankingFirestoreFalso : RankingFirestore() {
    private val itens = MutableStateFlow(
        listOf(
            RankingRemoto("1", "Carlos", "FACIL", 1000, 50, 100L),
            RankingRemoto("2", "Ana", "FACIL", 1200, 40, 200L),
            RankingRemoto("3", "Gabriel", "MEDIO", 2000, 100, 300L)
        )
    )

    override suspend fun inicializarRankingPadrao() {}
    override fun buscarTodos(): Flow<List<RankingRemoto>> = itens
    override suspend fun inserir(ranking: RankingRemoto) {
        itens.value = itens.value + ranking.copy(id = "${itens.value.size + 1}")
    }
    override suspend fun deletar(id: String) {
        itens.value = itens.value.filter { it.id != id }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RankingViewModelTest {
    private lateinit var viewModel: RankingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val repo = RankingRepository(RankingFirestoreFalso())
        viewModel = RankingViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testOrdenacaoEEmpate() = runTest {
        val estado = viewModel.uiState.value
        assertEquals(3, estado.itensRanking.size)
        assertEquals("Gabriel", estado.itensRanking[0].item.nomeJogador)
        assertEquals("Ana", estado.itensRanking[1].item.nomeJogador)
        assertEquals("Carlos", estado.itensRanking[2].item.nomeJogador)
    }

    @Test
    fun testFiltroNivel() = runTest {
        viewModel.onFiltroNivelChange(Nivel.FACIL)
        val estado = viewModel.uiState.value
        assertEquals(2, estado.itensRanking.size)
        assertEquals("Ana", estado.itensRanking[0].item.nomeJogador)
        assertEquals("Carlos", estado.itensRanking[1].item.nomeJogador)
    }
}
