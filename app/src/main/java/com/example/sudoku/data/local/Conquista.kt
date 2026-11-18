package com.example.sudoku.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "conquistas")
data class Conquista(
    @PrimaryKey
    val codigo: String,
    val desbloqueadaEm: Long = System.currentTimeMillis(),
    val vista: Boolean = false
)
