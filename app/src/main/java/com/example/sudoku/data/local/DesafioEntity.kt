package com.example.sudoku.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "desafios")
data class DesafioEntity(
    @PrimaryKey
    val id: String,
    val titulo: String,
    val descricao: String,
    val tipo: String,
    val tempoLimiteSegundos: Int = 0,
    val casasVazias: Int = 45,
    val dicas: Int = 1,
    val maxErros: Int = 3,
    val ativo: Boolean = true,
    val ordem: Int = 0,
    val concluido: Boolean = false,
    val tamanho: Int = 9
)
