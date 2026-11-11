package com.example.sudoku.logic

enum class Nivel(
    val titulo: String,
    val tamanho: Int,
    val linhasBloco: Int,
    val colunasBloco: Int,
    val casasVazias: Int,
    val dicas: Int,
    val pontosBase: Int
) {
    FACIL("Fácil", 4, 2, 2, 8, 3, 1000),
    MEDIO("Médio", 6, 2, 3, 18, 2, 2000),
    DIFICIL("Difícil", 9, 3, 3, 45, 1, 3000);

    companion object {
        fun deTexto(texto: String): Nivel =
            entries.firstOrNull { it.name == texto } ?: FACIL
    }
}
