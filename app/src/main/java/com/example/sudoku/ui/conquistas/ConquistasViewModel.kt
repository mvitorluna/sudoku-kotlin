package com.example.sudoku.ui.conquistas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.data.local.Conquista
import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.repository.ConquistaRepository
import com.example.sudoku.data.repository.Resultado
import com.example.sudoku.logic.RegrasConquistas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConquistasViewModel(
    private val repository: ConquistaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConquistasUiState())
    val uiState: StateFlow<ConquistasUiState> = _uiState.asStateFlow()

    private var todosOsItens: List<ConquistaItem> = emptyList()

    init {
        viewModelScope.launch {
            combine(repository.getAllPartidas(), repository.getAllConquistas()) { partidas, conquistas ->
                partidas to conquistas
            }.collect { (resultadoPartidas, resultadoConquistas) ->
                when {
                    resultadoPartidas is Resultado.Erro ->
                        _uiState.update { it.copy(erro = resultadoPartidas.mensagem) }
                    resultadoConquistas is Resultado.Erro ->
                        _uiState.update { it.copy(erro = resultadoConquistas.mensagem) }
                    resultadoPartidas is Resultado.Sucesso && resultadoConquistas is Resultado.Sucesso -> {
                        todosOsItens = montarItens(resultadoPartidas.dados, resultadoConquistas.dados)
                        aplicarFiltro()
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.existeNovaNaoVista().collect { temNova ->
                _uiState.update { it.copy(temNovaNaoVista = temNova) }
            }
        }
    }

    fun onTelaAberta() {
        viewModelScope.launch {
            val resultado = repository.marcarComoVistas()
            if (resultado is Resultado.Erro) _uiState.update { it.copy(erro = resultado.mensagem) }
        }
    }

    fun onFiltroChange(filtro: FiltroConquista) {
        _uiState.update { it.copy(filtro = filtro) }
        aplicarFiltro()
    }

    fun onPedirReset() {
        _uiState.update { it.copy(confirmandoReset = true) }
    }

    fun onCancelarReset() {
        _uiState.update { it.copy(confirmandoReset = false) }
    }

    fun onConfirmarReset() {
        _uiState.update { it.copy(confirmandoReset = false) }
        viewModelScope.launch {
            val resultado = repository.resetar()
            if (resultado is Resultado.Erro) _uiState.update { it.copy(erro = resultado.mensagem) }
        }
    }

    fun onErroVisto() {
        _uiState.update { it.copy(erro = null) }
    }

    private fun montarItens(partidas: List<Partida>, conquistas: List<Conquista>): List<ConquistaItem> {
        val salvas = conquistas.associateBy { it.codigo }
        return RegrasConquistas.todas.map { regra ->
            ConquistaItem(
                regra = regra,
                progresso = regra.progressoAtual(partidas),
                desbloqueadaEm = salvas[regra.codigo]?.desbloqueadaEm
            )
        }
    }

    private fun aplicarFiltro() {
        val filtro = _uiState.value.filtro
        val lista = todosOsItens
            .filter {
                when (filtro) {
                    FiltroConquista.TODAS -> true
                    FiltroConquista.DESBLOQUEADAS -> it.desbloqueada
                    FiltroConquista.BLOQUEADAS -> !it.desbloqueada
                }
            }
            .sortedWith(
                compareByDescending<ConquistaItem> { it.desbloqueada }
                    .thenByDescending { it.percentual }
            )
        _uiState.update {
            it.copy(
                listaDeConquistas = lista,
                totalDesbloqueadas = todosOsItens.count { item -> item.desbloqueada }
            )
        }
    }
}
