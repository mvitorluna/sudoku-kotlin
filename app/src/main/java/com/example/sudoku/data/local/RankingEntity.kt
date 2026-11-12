package com.example.sudoku.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "ranking")
data class RankingEntity(
    @PrimaryKey
    val id: String,
    val nomeJogador: String,
    val nivel: String,
    val pontos: Int,
    val tempoSegundos: Int,
    val data: Long
)
