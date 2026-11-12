package com.example.sudoku.data.remote

data class RankingRemoto(
    val id: String = "",
    val nomeJogador: String = "",
    val nivel: String = "MEDIO",
    val pontos: Int = 0,
    val tempoSegundos: Int = 0,
    val data: Long = System.currentTimeMillis()
)
