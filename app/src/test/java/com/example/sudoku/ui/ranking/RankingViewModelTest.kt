package com.example.sudoku.ui.ranking

import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.logic.Nivel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RankingDaoFalso : com.example.sudoku.data.local.RankingDao {
    private val itens = mutableListOf(
        com.example.sudoku.data.local.RankingEntity("1", "Carlos", "FACIL", 1000, 50, 100L),
        com.example.sudoku.data.local.RankingEntity("2", "Ana", "FACIL", 1200, 40, 200L),
        com.example.sudoku.data.local.RankingEntity("3", "Gabriel", "MEDIO", 2000, 100, 300L)
    )

    override fun buscarTodos() = kotlinx.coroutines.flow.flowOf(itens)
    override suspend fun buscarTodosAgora() = itens
    override suspend fun inserir(ranking: com.example.sudoku.data.local.RankingEntity) {
        itens.removeIf { it.id == ranking.id }
        itens.add(ranking)
    }
    override suspend fun deletar(id: String) {
        itens.removeIf { it.id == id }
    }
    override suspend fun contar() = itens.size
}

@OptIn(ExperimentalCoroutinesApi::class)
class RankingViewModelTest {

    private lateinit var viewModel: RankingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val repo = RankingRepository(com.example.sudoku.data.remote.RankingFirestore(RankingDaoFalso()))
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
