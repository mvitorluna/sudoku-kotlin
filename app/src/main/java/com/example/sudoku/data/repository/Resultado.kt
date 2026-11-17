package com.example.sudoku.data.repository

// ╔══════════════════════════════════════════════╗
// ║  Resultado.kt                                ║
// ║  Resultado de uma operação de dados          ║
// ║  Sucesso(dados) ou Erro(mensagem)            ║
// ╚══════════════════════════════════════════════╝

sealed class Resultado<out T> {
    data class Sucesso<T>(val dados: T) : Resultado<T>()
    data class Erro(val mensagem: String) : Resultado<Nothing>()
}
