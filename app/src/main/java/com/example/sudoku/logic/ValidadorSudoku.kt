package com.example.sudoku.logic

object ValidadorSudoku {

    fun temConflito(tabuleiro: List<List<Int>>, linha: Int, coluna: Int, nivel: Nivel): Boolean {
        val valor = tabuleiro[linha][coluna]
        if (valor == 0) return false

        val n = nivel.tamanho
        val bl = nivel.linhasBloco
        val bc = nivel.colunasBloco

        for (c in 0 until n) {
            if (c != coluna && tabuleiro[linha][c] == valor) return true
        }

        for (r in 0 until n) {
            if (r != linha && tabuleiro[r][coluna] == valor) return true
        }

        val inicioLinha = (linha / bl) * bl
        val inicioColuna = (coluna / bc) * bc
        for (r in inicioLinha until inicioLinha + bl) {
            for (c in inicioColuna until inicioColuna + bc) {
                if ((r != linha || c != coluna) && tabuleiro[r][c] == valor) return true
            }
        }

        return false
    }

    fun conflitos(tabuleiro: List<List<Int>>, nivel: Nivel): Set<Pair<Int, Int>> {
        val resultado = mutableSetOf<Pair<Int, Int>>()
        val n = nivel.tamanho
        for (r in 0 until n) {
            for (c in 0 until n) {
                if (temConflito(tabuleiro, r, c, nivel)) {
                    resultado.add(r to c)
                }
            }
        }
        return resultado
    }

    fun estaCompleto(tabuleiro: List<List<Int>>): Boolean =
        tabuleiro.all { linha -> linha.none { it == 0 } }

    fun venceu(tabuleiro: List<List<Int>>, nivel: Nivel): Boolean =
        estaCompleto(tabuleiro) && conflitos(tabuleiro, nivel).isEmpty()
}
