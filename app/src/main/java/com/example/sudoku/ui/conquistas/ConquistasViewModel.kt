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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConquistasViewModel(
    private val repository: ConquistaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConquistasUiState())
    val uiState: StateFlow<ConquistasUiState> = _uiState.asStateFlow()

    private var partidasSalvas: List<Partida> = emptyList()
    private var conquistasSalvas: List<Conquista> = emptyList()
    private var todosOsItens: List<ConquistaItem> = emptyList()

    init {
        viewModelScope.launch {
            repository.getAllPartidas().collect { resultado ->
                when (resultado) {
                    is Resultado.Sucesso -> {
                        partidasSalvas = resultado.dados
                        atualizarItens()
                    }
                    is Resultado.Erro -> _uiState.update { it.copy(erro = resultado.mensagem) }
                }
            }
        }
        viewModelScope.launch {
            repository.getAllConquistas().collect { resultado ->
                when (resultado) {
                    is Resultado.Sucesso -> {
                        conquistasSalvas = resultado.dados
                        atualizarItens()
                    }
                    is Resultado.Erro -> _uiState.update { it.copy(erro = resultado.mensagem) }
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

    private fun atualizarItens() {
        val salvas = conquistasSalvas.associateBy { it.codigo }
        todosOsItens = RegrasConquistas.todas.map { regra ->
            ConquistaItem(
                regra = regra,
                progresso = regra.progressoAtual(partidasSalvas),
                desbloqueadaEm = salvas[regra.codigo]?.desbloqueadaEm
            )
        }
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val filtro = _uiState.value.filtro
        val desbloqueadas = todosOsItens.filter { it.desbloqueada }
        val bloqueadas = todosOsItens
            .filter { !it.desbloqueada }
            .sortedByDescending { it.percentual }

        val lista = when (filtro) {
            FiltroConquista.TODAS -> desbloqueadas + bloqueadas
            FiltroConquista.DESBLOQUEADAS -> desbloqueadas
            FiltroConquista.BLOQUEADAS -> bloqueadas
        }

        _uiState.update {
            it.copy(listaDeConquistas = lista, totalDesbloqueadas = desbloqueadas.size)
        }
    }
}
