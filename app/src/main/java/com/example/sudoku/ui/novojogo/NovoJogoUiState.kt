package com.example.sudoku.ui.novojogo

import com.example.sudoku.logic.Nivel

data class ResumoNivel(
    val nivel: Nivel,
    val partidasJogadas: Int = 0,
    val vitorias: Int = 0,
    val recordePontos: Int? = null,
    val melhorTempo: Int? = null
) {
    val jaJogou: Boolean
        get() = partidasJogadas > 0
}

data class NovoJogoUiState(
    val resumos: List<ResumoNivel> = Nivel.entries.map { ResumoNivel(it) },
    val nivelSelecionado: Nivel = Nivel.FACIL,
    val erro: String? = null
) {
    val textoBotao: String
        get() = "Jogar ${nivelSelecionado.titulo} ${nivelSelecionado.tamanho}x${nivelSelecionado.tamanho}"
}
