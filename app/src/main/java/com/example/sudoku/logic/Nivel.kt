package com.example.sudoku.logic

// ╔══════════════════════════════════════════════╗
// ║  Nivel.kt                                    ║
// ║  Os 3 níveis do jogo                         ║
// ║  Tamanho, blocos, casas vazias, dicas        ║
// ╚══════════════════════════════════════════════╝

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

    val nomeComTamanho: String
        get() = "$titulo ${tamanho}x$tamanho"

    companion object {
        fun deTexto(texto: String): Nivel =
            when (texto) {
                "MEDIO" -> MEDIO
                "DIFICIL" -> DIFICIL
                else -> FACIL
            }
    }
}
