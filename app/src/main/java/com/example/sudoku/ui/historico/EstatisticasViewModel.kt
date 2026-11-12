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

class EstatisticasViewModel(
    private val repository: PartidaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstatisticasUiState())
    val uiState: StateFlow<EstatisticasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllPartidas().collect { resultado ->
                when (resultado) {
                    is Resultado.Sucesso -> calcular(resultado.dados)
                    is Resultado.Erro -> _uiState.update { it.copy(erro = resultado.mensagem) }
                }
            }
        }
    }

    fun onErroVisto() {
        _uiState.update { it.copy(erro = null) }
    }

    private fun calcular(partidas: List<Partida>) {
        val porNivel = partidas.groupBy { it.nivel }
        val estatisticas = Nivel.entries.map { nivel ->
            val doNivel = porNivel[nivel.name].orEmpty()
            val vitorias = doNivel.filter { it.venceu }
            EstatisticaNivel(
                nivel = nivel,
                jogadas = doNivel.size,
                vitorias = vitorias.size,
                melhorTempo = vitorias.minOfOrNull { it.tempoSegundos },
                mediaPontos = if (vitorias.isEmpty()) null else vitorias.map { it.pontos }.average()
            )
        }
        _uiState.update {
            it.copy(
                totalPartidas = partidas.size,
                vitorias = partidas.count { partida -> partida.venceu },
                sequenciaAtual = partidas.sortedByDescending { partida -> partida.data }
                    .takeWhile { partida -> partida.venceu }
                    .size,
                porNivel = estatisticas
            )
        }
    }
}
