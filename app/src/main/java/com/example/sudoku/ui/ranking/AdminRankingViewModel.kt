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

class AdminRankingViewModel(
    private val repository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminRankingUiState())
    val uiState: StateFlow<AdminRankingUiState> = _uiState.asStateFlow()

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
                        processarDados()
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
        processarDados()
    }

    fun onBuscaNomeChange(busca: String) {
        _uiState.update { it.copy(buscaNome = busca) }
        processarDados()
    }

    fun onPedirExclusao(item: RankingRemoto) {
        _uiState.update { it.copy(confirmandoExclusao = item) }
    }

    fun onCancelarExclusao() {
        _uiState.update { it.copy(confirmandoExclusao = null) }
    }

    fun onConfirmarExclusao() {
        val item = _uiState.value.confirmandoExclusao ?: return
        _uiState.update { it.copy(confirmandoExclusao = null) }

        viewModelScope.launch {
            when (val resultado = repository.deletar(item.id)) {
                is Resultado.Sucesso -> {
                    // Flow automático atualiza a lista, mas podemos garantir se necessário
                }
                is Resultado.Erro -> {
                    _uiState.update { it.copy(erro = resultado.mensagem) }
                }
            }
        }
    }

    fun onErroVisto() {
        _uiState.update { it.copy(erro = null) }
    }

    private fun processarDados() {
        val estatisticas = Nivel.entries.map { nivel ->
            val itensDoNivel = todosOsItens.filter { it.nivel == nivel.name }
            val total = itensDoNivel.size
            val media = if (total > 0) itensDoNivel.map { it.pontos }.average() else 0.0
            val melhorTempo = if (total > 0) itensDoNivel.minOfOrNull { it.tempoSegundos } else null
            
            val maisFrequente = if (total > 0) {
                itensDoNivel.groupBy { it.nomeJogador }
                    .maxByOrNull { it.value.size }?.key
            } else null

            EstatisticaRankingNivel(
                nivel = nivel,
                totalRegistros = total,
                mediaPontos = media,
                melhorTempo = melhorTempo,
                maisFrequente = maisFrequente
            )
        }

        val estado = _uiState.value
        val filtrados = todosOsItens
            .filter { estado.filtroNivel == null || it.nivel == estado.filtroNivel.name }
            .filter { estado.buscaNome.isBlank() || it.nomeJogador.contains(estado.buscaNome, ignoreCase = true) }
            .sortedByDescending { it.data }

        _uiState.update {
            it.copy(
                listaRanking = filtrados,
                estatisticasPorNivel = estatisticas,
                carregando = false,
                erro = null
            )
        }
    }
}
