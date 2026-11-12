package com.example.sudoku.ui.desafios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.data.local.DesafioEntity
import com.example.sudoku.data.repository.DesafioRepository
import com.example.sudoku.data.repository.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DesafiosViewModel(
    private val repository: DesafioRepository,
    private val apenasAtivos: Boolean = true
) : ViewModel() {

    private val _uiState = MutableStateFlow(DesafiosUiState())
    val uiState: StateFlow<DesafiosUiState> = _uiState.asStateFlow()

    init {
        carregarDesafios()
    }

    fun carregarDesafios() {
        viewModelScope.launch {
            repository.inicializarDesafiosPadrao()
            val flow = if (apenasAtivos) {
                repository.getDesafiosAtivos()
            } else {
                repository.getTodosDesafios()
            }
            flow.collect { resultado ->
                when (resultado) {
                    is Resultado.Sucesso -> {
                        _uiState.update {
                            it.copy(
                                desafios = resultado.dados,
                                carregando = false,
                                erro = null
                            )
                        }
                    }
                    is Resultado.Erro -> {
                        _uiState.update {
                            it.copy(
                                carregando = false,
                                erro = resultado.mensagem
                            )
                        }
                    }
                }
            }
        }
    }

    fun alternarAtivo(desafio: DesafioEntity) {
        viewModelScope.launch {
            val atualizado = desafio.copy(ativo = !desafio.ativo)
            repository.atualizar(atualizado)
        }
    }

    fun atualizarDesafio(desafio: DesafioEntity) {
        viewModelScope.launch {
            repository.atualizar(desafio)
        }
    }

    fun onErroVisto() {
        _uiState.update { it.copy(erro = null) }
    }
}
