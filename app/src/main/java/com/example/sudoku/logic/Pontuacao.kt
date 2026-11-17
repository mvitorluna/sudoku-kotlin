package com.example.sudoku.logic

// ╔══════════════════════════════════════════════╗
// ║  Pontuacao.kt                                ║
// ║  Cálculo dos pontos no fim da partida        ║
// ║  base - tempo - erros - dicas                ║
// ╚══════════════════════════════════════════════╝

object Pontuacao {

    fun calcular(nivel: Nivel, segundos: Int, erros: Int, dicas: Int): Int {
        val total = nivel.pontosBase - segundos - (50 * erros) - (100 * dicas)
        return total.coerceAtLeast(0)
    }
}
