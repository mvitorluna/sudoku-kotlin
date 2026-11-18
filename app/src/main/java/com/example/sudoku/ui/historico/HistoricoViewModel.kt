package com.example.sudoku.ui.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.data.repository.Resultado
import com.example.sudoku.logic.Nivel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoricoViewModel(
    private val repository: PartidaRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoricoUiState())
    val uiState: StateFlow<HistoricoUiState> = _uiState.asStateFlow()

    private var todasAsPartidas: List<Partida> = emptyList()

    init {
        viewModelScope.launch {
            repository.getAllPartidas().collect { resultado ->
                when (resultado) {
                    is Resultado.Sucesso -> {
                        todasAsPartidas = resultado.dados
                        aplicarFiltros()
                    }
                    is Resultado.Erro -> _uiState.update { it.copy(erro = resultado.mensagem) }
                }
            }
        }
    }

    fun onFiltroNivelChange(nivel: Nivel?) {
        _uiState.update { it.copy(filtroNivel = nivel) }
        aplicarFiltros()
    }

    fun onSoVitoriasChange() {
        _uiState.update { it.copy(soVitorias = !it.soVitorias) }
        aplicarFiltros()
    }

    fun onSoFavoritasChange() {
        _uiState.update { it.copy(soFavoritas = !it.soFavoritas) }
        aplicarFiltros()
    }

    fun onSoDesafiosChange() {
        _uiState.update { it.copy(soDesafios = !it.soDesafios) }
        aplicarFiltros()
    }

    fun onOrdenacaoChange(ordenacao: OrdenacaoPartidas) {
        _uiState.update { it.copy(ordenacao = ordenacao) }
        aplicarFiltros()
    }

    fun onPartidaClick(partida: Partida) {
        _uiState.update {
            it.copy(partidaExpandidaId = if (it.partidaExpandidaId == partida.id) null else partida.id)
        }
    }

    fun onFavoritar(partida: Partida) {
        viewModelScope.launch {
            val resultado = repository.atualizar(partida.copy(favorita = !partida.favorita))
            if (resultado is Resultado.Erro) _uiState.update { it.copy(erro = resultado.mensagem) }
        }
    }

    fun onDelete(partida: Partida) {
        viewModelScope.launch {
            val resultado = repository.deletar(partida)
            if (resultado is Resultado.Erro) _uiState.update { it.copy(erro = resultado.mensagem) }
        }
    }

    fun onPedirLimpar() {
        _uiState.update { it.copy(confirmandoLimpar = true) }
    }

    fun onCancelarLimpar() {
        _uiState.update { it.copy(confirmandoLimpar = false) }
    }

    fun onConfirmarLimpar() {
        _uiState.update { it.copy(confirmandoLimpar = false) }
        viewModelScope.launch {
            val resultado = repository.deletarTodos()
            if (resultado is Resultado.Erro) _uiState.update { it.copy(erro = resultado.mensagem) }
        }
    }

    fun onErroVisto() {
        _uiState.update { it.copy(erro = null) }
    }

    private fun aplicarFiltros() {
        val estado = _uiState.value
        val filtradas = todasAsPartidas
            .filter { estado.filtroNivel == null || it.nivel == estado.filtroNivel.name }
            .filter { !estado.soVitorias || it.venceu }
            .filter { !estado.soFavoritas || it.favorita }
            .filter { !estado.soDesafios || it.semente != 0L }
        val lista = when (estado.ordenacao) {
            OrdenacaoPartidas.MAIS_RECENTE -> filtradas.sortedByDescending { it.data }
            OrdenacaoPartidas.MENOR_TEMPO -> filtradas.sortedBy { it.tempoSegundos }
            OrdenacaoPartidas.MAIS_PONTOS -> filtradas.sortedByDescending { it.pontos }
        }
        _uiState.update { it.copy(listaDePartidas = lista) }
    }
}
