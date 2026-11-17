package com.example.sudoku.ui.jogo

import com.example.sudoku.data.local.Conquista
import com.example.sudoku.data.local.ConquistaDAO
import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.local.PartidaDAO
import com.example.sudoku.data.repository.ConquistaRepository
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.logic.GeradorSudoku
import com.example.sudoku.ui.ranking.RankingFirestoreFalso
import com.example.sudoku.logic.Nivel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class JogoViewModelTest {

    // Mesma semente nos testes e no ViewModel = mesmo Sudoku, então o teste sabe o gabarito
    private val semente = 42L
    private val nivel = Nivel.FACIL
    private val gabarito = GeradorSudoku.gerar(nivel, Random(semente)).gabarito

    private lateinit var partidaDao: PartidaDAOFalso
    private lateinit var viewModel: JogoViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        partidaDao = PartidaDAOFalso()
        viewModel = JogoViewModel(
            nivel,
            semente,
            PartidaRepository(partidaDao),
            ConquistaRepository(ConquistaDAOFalso(), partidaDao),
            RankingRepository(RankingFirestoreFalso())
        )
    }

    @After
    fun limpar() {
        Dispatchers.resetMain()
    }

    private fun casasVazias(): List<Pair<Int, Int>> {
        val tabuleiro = viewModel.uiState.value.tabuleiro
        return tabuleiro.indices.flatMap { l -> tabuleiro[l].indices.map { c -> Pair(l, c) } }
            .filter { (l, c) -> tabuleiro[l][c] == 0 }
    }

    // Casa vazia numa linha que já tem número, para conseguir provocar um erro
    private fun casaVaziaComNumeroNaLinha(): Pair<Int, Int> =
        casasVazias().first { (l, _) -> viewModel.uiState.value.tabuleiro[l].any { it != 0 } }

    // Um número que já existe na mesma linha da casa: sempre gera conflito
    private fun numeroRepetidoNaLinha(linha: Int): Int =
        viewModel.uiState.value.tabuleiro[linha].first { it != 0 }

    @Test
    fun comecaComAsCasasVaziasEDicasDoNivel() {
        val estado = viewModel.uiState.value
        assertEquals(nivel.casasVazias, estado.casasRestantes)
        assertEquals(nivel.dicas, estado.dicasRestantes)
        assertEquals(nivel.tamanho * nivel.tamanho - nivel.casasVazias, estado.fixas.size)
        assertEquals(StatusJogo.EM_ANDAMENTO, estado.status)
    }

    @Test
    fun casaFixaPodeSerSelecionadaMasNaoMuda() {
        val fixa = viewModel.uiState.value.fixas.first()
        val valorOriginal = viewModel.uiState.value.tabuleiro[fixa.first][fixa.second]
        viewModel.onCasaClick(fixa.first, fixa.second)
        assertEquals(fixa, viewModel.uiState.value.selecionada)

        viewModel.onNumeroClick(if (valorOriginal == 1) 2 else 1)
        viewModel.onApagar()
        assertEquals(valorOriginal, viewModel.uiState.value.tabuleiro[fixa.first][fixa.second])
        assertEquals(0, viewModel.uiState.value.erros)
    }

    @Test
    fun numeroCertoNaoContaErro() {
        val (l, c) = casasVazias().first()
        viewModel.onCasaClick(l, c)
        viewModel.onNumeroClick(gabarito[l][c])
        assertEquals(0, viewModel.uiState.value.erros)
        assertEquals(gabarito[l][c], viewModel.uiState.value.tabuleiro[l][c])
    }

    @Test
    fun numeroRepetidoContaErroEFicaVermelho() {
        val (l, c) = casaVaziaComNumeroNaLinha()
        viewModel.onCasaClick(l, c)
        viewModel.onNumeroClick(numeroRepetidoNaLinha(l))
        assertEquals(1, viewModel.uiState.value.erros)
        assertTrue(Pair(l, c) in viewModel.uiState.value.conflitos)
    }

    @Test
    fun apagarLimpaACasaEOConflito() {
        val (l, c) = casaVaziaComNumeroNaLinha()
        viewModel.onCasaClick(l, c)
        viewModel.onNumeroClick(numeroRepetidoNaLinha(l))
        viewModel.onApagar()
        assertEquals(0, viewModel.uiState.value.tabuleiro[l][c])
        assertTrue(viewModel.uiState.value.conflitos.isEmpty())
    }

    @Test
    fun terceiroErroPerdeESalvaAPartida() {
        val (l, c) = casaVaziaComNumeroNaLinha()
        viewModel.onCasaClick(l, c)
        repeat(3) { viewModel.onNumeroClick(numeroRepetidoNaLinha(l)) }

        val estado = viewModel.uiState.value
        assertEquals(StatusJogo.PERDEU, estado.status)
        assertEquals(0, estado.pontos)
        assertEquals(1, partidaDao.salvas.size)
        assertFalse(partidaDao.salvas.first().venceu)
    }

    @Test
    fun dicaPreencheComOGabaritoEViraFixa() {
        val fixasAntes = viewModel.uiState.value.fixas
        viewModel.onDica()

        val estado = viewModel.uiState.value
        val (l, c) = (estado.fixas - fixasAntes).single()
        assertEquals(nivel.dicas - 1, estado.dicasRestantes)
        assertEquals(nivel.casasVazias - 1, estado.casasRestantes)
        assertEquals(gabarito[l][c], estado.tabuleiro[l][c])
    }

    @Test
    fun preencherTudoCertoVencePontuaESalva() {
        casasVazias().forEach { (l, c) ->
            viewModel.onCasaClick(l, c)
            viewModel.onNumeroClick(gabarito[l][c])
        }

        val estado = viewModel.uiState.value
        assertEquals(StatusJogo.VENCEU, estado.status)
        assertEquals(nivel.pontosBase - estado.segundos, estado.pontos)
        assertTrue(partidaDao.salvas.first().venceu)
        assertEquals(semente, partidaDao.salvas.first().semente)
        assertTrue(estado.conquistasNovas.any { it.codigo == "PRIMEIRA_VITORIA" })
    }

    @Test
    fun publicarExigeNomeValidoESoUmaVez() {
        casasVazias().forEach { (l, c) ->
            viewModel.onCasaClick(l, c)
            viewModel.onNumeroClick(gabarito[l][c])
        }

        viewModel.onNomeChange("A")
        viewModel.onPublicarRanking()
        assertFalse(viewModel.uiState.value.publicado)
        assertEquals("Digite um nome de 2 a 20 letras", viewModel.uiState.value.erro)

        viewModel.onNomeChange("Eduardo")
        viewModel.onPublicarRanking()
        assertTrue(viewModel.uiState.value.publicado)
        assertFalse(viewModel.uiState.value.podePublicar)
    }
}

private class PartidaDAOFalso : PartidaDAO {
    val salvas = mutableListOf<Partida>()
    private val fluxo = MutableStateFlow<List<Partida>>(emptyList())

    override suspend fun inserir(partida: Partida) {
        salvas.add(partida)
        fluxo.value = salvas.toList()
    }

    override fun buscarTodos(): Flow<List<Partida>> = fluxo

    override suspend fun buscarTodosAgora(): List<Partida> = salvas.toList()

    override suspend fun atualizar(partida: Partida) {}

    override suspend fun deletar(partida: Partida) {}

    override suspend fun deletarTodos() {
        salvas.clear()
    }
}

private class ConquistaDAOFalso : ConquistaDAO {
    private val salvas = mutableListOf<Conquista>()

    override suspend fun inserir(conquista: Conquista) {
        if (salvas.none { it.codigo == conquista.codigo }) salvas.add(conquista)
    }

    override fun buscarTodos(): Flow<List<Conquista>> = MutableStateFlow(salvas.toList())

    override suspend fun buscarTodosAgora(): List<Conquista> = salvas.toList()

    override suspend fun atualizar(conquista: Conquista) {}

    override suspend fun deletarTodos() {
        salvas.clear()
    }
}
