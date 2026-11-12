package com.example.sudoku.ui.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.data.remote.RankingRemoto
import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.data.repository.Resultado
import com.example.sudoku.logic.Nivel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingViewModel(
    private val repository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    private var todosOsItens: List<RankingRemoto> = emptyList()

    init {
        carregarRanking()
    }

    fun carregarRanking() {
        _uiState.update { it.copy(carregando = true, erro = null) }
        viewModelScope.launch {
            repository.inicializarRankingPadrao()
            repository.getRanking().collect { resultado ->
                when (resultado) {
                    is Resultado.Sucesso -> {
                        todosOsItens = resultado.dados
                        aplicarFiltrosEOrdenacao()
                    }
                    is Resultado.Erro -> {
                        _uiState.update { it.copy(carregando = false, erro = resultado.mensagem) }
                    }
                }
            }
        }
    }

    fun onFiltroNivelChange(nivel: Nivel?) {
        _uiState.update { it.copy(filtroNivel = nivel) }
        aplicarFiltrosEOrdenacao()
    }

    fun onBuscaNomeChange(busca: String) {
        _uiState.update { it.copy(buscaNome = busca) }
        aplicarFiltrosEOrdenacao()
    }

    fun onErroVisto() {
        _uiState.update { it.copy(erro = null) }
    }

    private fun aplicarFiltrosEOrdenacao() {
        val estado = _uiState.value
        val filtrados = todosOsItens
            .filter { estado.filtroNivel == null || it.nivel == estado.filtroNivel.name }
            .filter { estado.buscaNome.isBlank() || it.nomeJogador.contains(estado.buscaNome, ignoreCase = true) }

        // Ordenar por pontos desc, e desempata pelo menor tempo asc
        val ordenados = filtrados.sortedWith(
            compareByDescending<RankingRemoto> { it.pontos }
                .thenBy { it.tempoSegundos }
        )

        val top20 = ordenados.take(20)

        val itensUi = top20.mapIndexed { index, item ->
            RankingItemUi(
                posicao = index + 1,
                item = item
            )
        }

        _uiState.update {
            it.copy(
                itensRanking = itensUi,
                carregando = false,
                erro = null
            )
        }
    }
}
