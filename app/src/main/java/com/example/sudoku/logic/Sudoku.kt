package com.example.sudoku.logic

// ╔══════════════════════════════════════════════╗
// ║  Sudoku.kt                                   ║
// ║  Um Sudoku gerado                            ║
// ║  Tabuleiro (com vazias) + gabarito           ║
// ╚══════════════════════════════════════════════╝

data class Sudoku(
    val nivel: Nivel,
    val tabuleiro: List<List<Int>>,
    val gabarito: List<List<Int>>
)
