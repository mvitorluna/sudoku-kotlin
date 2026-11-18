package com.example.sudoku.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "partidas")
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nivel: String,
    val venceu: Boolean,
    val tempoSegundos: Int,
    val erros: Int,
    val dicasUsadas: Int,
    val pontos: Int,
    val semente: Long = 0,
    val favorita: Boolean = false,
    val data: Long = System.currentTimeMillis()
)
