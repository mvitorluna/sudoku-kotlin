package com.example.sudoku.ui.desafios

import com.example.sudoku.data.local.DesafioEntity

data class DesafiosUiState(
    val desafios: List<DesafioEntity> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)
