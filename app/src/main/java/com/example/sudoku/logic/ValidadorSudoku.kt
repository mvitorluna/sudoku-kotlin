package com.example.sudoku.logic

// ╔══════════════════════════════════════════════╗
// ║  ValidadorSudoku.kt                          ║
// ║  Regras de validação do tabuleiro            ║
// ║  Conflitos, grupos completos, vitória        ║
// ╚══════════════════════════════════════════════╝

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

    // Casas que fazem parte de uma linha, coluna ou bloco totalmente preenchido e sem conflito.
    // A tela usa isso para fazer a animação de "completou uma linha".
    fun casasEmGruposCompletos(tabuleiro: List<List<Int>>, nivel: Nivel): Set<Pair<Int, Int>> {
        val n = nivel.tamanho
        val bl = nivel.linhasBloco
        val bc = nivel.colunasBloco
        val blocosPorFaixa = n / bc
        val comConflito = conflitos(tabuleiro, nivel)

        val linhas = (0 until n).map { l -> (0 until n).map { c -> l to c } }
        val colunas = (0 until n).map { c -> (0 until n).map { l -> l to c } }
        val blocos = (0 until n).map { b ->
            val l0 = (b / blocosPorFaixa) * bl
            val c0 = (b % blocosPorFaixa) * bc
            (0 until bl).flatMap { dl -> (0 until bc).map { dc -> (l0 + dl) to (c0 + dc) } }
        }

        return (linhas + colunas + blocos)
            .filter { grupo -> grupo.all { (l, c) -> tabuleiro[l][c] != 0 && (l to c) !in comConflito } }
            .flatten()
            .toSet()
    }

    fun estaCompleto(tabuleiro: List<List<Int>>): Boolean =
        tabuleiro.all { linha -> linha.none { it == 0 } }

    fun venceu(tabuleiro: List<List<Int>>, nivel: Nivel): Boolean =
        estaCompleto(tabuleiro) && conflitos(tabuleiro, nivel).isEmpty()
}
