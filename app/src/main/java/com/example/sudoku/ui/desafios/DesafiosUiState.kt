package com.example.sudoku.ui.desafios

// ╔══════════════════════════════════════════════╗
// ║  DesafiosUiState.kt                          ║
// ║  Estado das telas de desafios                ║
// ║  Lista, carregando e erro                    ║
// ╚══════════════════════════════════════════════╝

import com.example.sudoku.data.local.DesafioEntity

data class DesafiosUiState(
    val desafios: List<DesafioEntity> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)
