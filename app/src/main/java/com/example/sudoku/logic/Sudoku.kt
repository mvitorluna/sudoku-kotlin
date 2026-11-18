package com.example.sudoku.logic

data class Sudoku(
    val nivel: Nivel,
    val tabuleiro: List<List<Int>>,
    val gabarito: List<List<Int>>
)
