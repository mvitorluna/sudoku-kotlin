package com.example.sudoku.ui.jogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.repository.ConquistaRepository
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.data.repository.Resultado
import com.example.sudoku.logic.GeradorSudoku
import com.example.sudoku.logic.Nivel
import com.example.sudoku.logic.Pontuacao
import com.example.sudoku.logic.ValidadorSudoku
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class JogoViewModel(
    private val nivel: Nivel,
    private val semente: Long,
    private val partidaRepository: PartidaRepository,
    private val conquistaRepository: ConquistaRepository,
    private val rankingRepository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JogoUiState())
    val uiState: StateFlow<JogoUiState> = _uiState.asStateFlow()

    // O gabarito não vai para o UiState: a tela não pode saber a resposta
    private var gabarito: List<List<Int>> = emptyList()

    private var cronometro: Job? = null

    init {
        novoJogo()
    }

    fun onCasaClick(linha: Int, coluna: Int) {
        val estado = _uiState.value
        // Casa fixa também pode ser selecionada: serve para destacar o mesmo número no tabuleiro.
        // Quem impede de escrever nela é o onNumeroClick/onApagar.
        if (estado.acabou) return
        _uiState.update { it.copy(selecionada = Pair(linha, coluna)) }
    }

    fun onNumeroClick(numero: Int) {
        val estado = _uiState.value
        val (linha, coluna) = estado.selecionada ?: return
        if (estado.acabou || Pair(linha, coluna) in estado.fixas) return

        // Cria um tabuleiro novo: o StateFlow só avisa a tela quando recebe um objeto diferente
        val novoTabuleiro = estado.tabuleiro.map { it.toMutableList() }
        novoTabuleiro[linha][coluna] = numero

        val errou = ValidadorSudoku.temConflito(novoTabuleiro, linha, coluna, nivel)

        _uiState.update {
            it.copy(
                tabuleiro = novoTabuleiro,
                conflitos = ValidadorSudoku.conflitos(novoTabuleiro, nivel),
                erros = if (errou) it.erros + 1 else it.erros
            )
        }
        verificarFim()
    }

    fun onApagar() {
        val estado = _uiState.value
        val (linha, coluna) = estado.selecionada ?: return
        if (estado.acabou || Pair(linha, coluna) in estado.fixas) return

        val novoTabuleiro = estado.tabuleiro.map { it.toMutableList() }
        novoTabuleiro[linha][coluna] = 0

        _uiState.update {
            it.copy(
                tabuleiro = novoTabuleiro,
                conflitos = ValidadorSudoku.conflitos(novoTabuleiro, nivel)
            )
        }
    }

    fun onDica() {
        val estado = _uiState.value
        if (estado.acabou || estado.dicasRestantes == 0) return

        val vazias = estado.tabuleiro.indices
            .flatMap { l -> estado.tabuleiro[l].indices.map { c -> Pair(l, c) } }
            .filter { (l, c) -> estado.tabuleiro[l][c] == 0 }
        if (vazias.isEmpty()) return

        val (linha, coluna) = vazias.random()
        val novoTabuleiro = estado.tabuleiro.map { it.toMutableList() }
        novoTabuleiro[linha][coluna] = gabarito[linha][coluna]

        _uiState.update {
            it.copy(
                tabuleiro = novoTabuleiro,
                conflitos = ValidadorSudoku.conflitos(novoTabuleiro, nivel),
                // A casa da dica vira fixa: o jogador não pode apagar a resposta certa
                fixas = it.fixas + Pair(linha, coluna),
                dicasRestantes = it.dicasRestantes - 1,
                selecionada = null
            )
        }
        verificarFim()
    }

    fun onNomeChange(nome: String) {
        _uiState.update { it.copy(nomeParaRanking = nome) }
    }

    fun onPublicarRanking() {
        val estado = _uiState.value
        if (!estado.podePublicar) return

        val nome = estado.nomeParaRanking.trim()
        if (nome.length !in 2..20 || !nome.all { it.isLetter() || it == ' ' }) {
            _uiState.update { it.copy(erro = "Digite um nome de 2 a 20 letras") }
            return
        }

        viewModelScope.launch {
            val resultado = rankingRepository.publicar(nome, nivel.name, estado.pontos, estado.segundos)
            when (resultado) {
                is Resultado.Sucesso -> _uiState.update { it.copy(publicado = true, erro = null) }
                is Resultado.Erro -> _uiState.update { it.copy(erro = resultado.mensagem) }
            }
        }
    }

    fun onJogarDeNovo() {
        novoJogo()
    }

    fun onPedirSair() {
        _uiState.update { it.copy(confirmandoSaida = true) }
    }

    fun onCancelarSaida() {
        _uiState.update { it.copy(confirmandoSaida = false) }
    }

    fun onErroVisto() {
        _uiState.update { it.copy(erro = null) }
    }

    private fun novoJogo() {
        // Semente 0 = jogo aleatório. Outra semente = desafio, sempre o mesmo Sudoku
        val random = if (semente == 0L) Random.Default else Random(semente)
        val sudoku = GeradorSudoku.gerar(nivel, random)
        gabarito = sudoku.gabarito

        val fixas = sudoku.tabuleiro.indices
            .flatMap { l -> sudoku.tabuleiro[l].indices.map { c -> Pair(l, c) } }
            .filter { (l, c) -> sudoku.tabuleiro[l][c] != 0 }
            .toSet()

        _uiState.value = JogoUiState(
            nivel = nivel,
            semente = semente,
            tabuleiro = sudoku.tabuleiro,
            fixas = fixas,
            dicasRestantes = nivel.dicas
        )
        iniciarCronometro()
    }

    // O cronômetro fica no ViewModel para continuar contando quando a tela gira
    private fun iniciarCronometro() {
        cronometro?.cancel()
        cronometro = viewModelScope.launch {
            while (!_uiState.value.acabou) {
                delay(1000)
                _uiState.update { it.copy(segundos = it.segundos + 1) }
            }
        }
    }

    private fun verificarFim() {
        val estado = _uiState.value
        when {
            estado.erros >= MAXIMO_DE_ERROS -> finalizar(StatusJogo.PERDEU)
            ValidadorSudoku.venceu(estado.tabuleiro, nivel) -> finalizar(StatusJogo.VENCEU)
        }
    }

    private fun finalizar(status: StatusJogo) {
        cronometro?.cancel()

        val estado = _uiState.value
        val pontos = if (status == StatusJogo.VENCEU) {
            Pontuacao.calcular(nivel, estado.segundos, estado.erros, estado.dicasUsadas)
        } else {
            0
        }
        _uiState.update { it.copy(status = status, pontos = pontos, selecionada = null) }

        viewModelScope.launch {
            val partida = Partida(
                nivel = nivel.name,
                venceu = status == StatusJogo.VENCEU,
                tempoSegundos = estado.segundos,
                erros = estado.erros,
                dicasUsadas = estado.dicasUsadas,
                pontos = pontos,
                semente = semente
            )
            val salvou = partidaRepository.inserir(partida)
            if (salvou is Resultado.Erro) {
                _uiState.update { it.copy(erro = salvou.mensagem) }
                return@launch
            }

            when (val conquistas = conquistaRepository.verificar()) {
                is Resultado.Sucesso -> _uiState.update { it.copy(conquistasNovas = conquistas.dados) }
                is Resultado.Erro -> _uiState.update { it.copy(erro = conquistas.mensagem) }
            }
        }
    }

    companion object {
        const val MAXIMO_DE_ERROS = 3
    }
}
