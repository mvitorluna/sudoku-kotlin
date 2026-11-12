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

    val textoRecorde: String
        get() = if (recordePontos == null) "-" else "$recordePontos pts"

    val textoMelhorTempo: String
        get() {
            if (melhorTempo == null) return "-"
            val minutos = (melhorTempo / 60).toString().padStart(2, '0')
            val segundos = (melhorTempo % 60).toString().padStart(2, '0')
            return "$minutos:$segundos"
        }
}

data class NovoJogoUiState(
    val resumos: List<ResumoNivel> = Nivel.entries.map { ResumoNivel(it) },
    val nivelSelecionado: Nivel = Nivel.FACIL,
    val erro: String? = null
) {
    val textoBotao: String
        get() = "Jogar ${nivelSelecionado.nomeComTamanho}"
}
