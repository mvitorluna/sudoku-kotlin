package com.example.sudoku.ui.novojogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.data.repository.Resultado
import com.example.sudoku.logic.Nivel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NovoJogoViewModel(
    private val repository: PartidaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovoJogoUiState())
    val uiState: StateFlow<NovoJogoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllPartidas().collect { resultado ->
                when (resultado) {
                    is Resultado.Erro -> _uiState.update { it.copy(erro = resultado.mensagem) }
                    is Resultado.Sucesso -> {
                        val resumos = Nivel.entries.map { nivel ->
                            val partidasDoNivel = resultado.dados.filter { it.nivel == nivel.name }
                            val vitoriasDoNivel = partidasDoNivel.filter { it.venceu }
                            ResumoNivel(
                                nivel = nivel,
                                partidasJogadas = partidasDoNivel.size,
                                vitorias = partidasDoNivel.count { it.venceu },
                                recordePontos = vitoriasDoNivel.maxOfOrNull { it.pontos },
                                melhorTempo = vitoriasDoNivel.minOfOrNull { it.tempoSegundos }
                            )
                        }
                        _uiState.update { it.copy(resumos = resumos) }
                    }
                }
            }
        }
    }

    fun onSelecionarNivel(nivel: Nivel) {
        _uiState.update { it.copy(nivelSelecionado = nivel) }
    }

    fun onErroVisto() {
        _uiState.update { it.copy(erro = null) }
    }
}
