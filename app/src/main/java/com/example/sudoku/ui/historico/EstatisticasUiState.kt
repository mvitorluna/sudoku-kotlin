package com.example.sudoku.ui.historico

// ╔══════════════════════════════════════════════╗
// ║  EstatisticasUiState.kt                      ║
// ║  Estado da sub-aba Estatísticas              ║
// ║  Totais e números por nível                  ║
// ╚══════════════════════════════════════════════╝

import com.example.sudoku.logic.Nivel

data class EstatisticaNivel(
    val nivel: Nivel,
    val jogadas: Int = 0,
    val vitorias: Int = 0,
    val melhorTempo: Int? = null,
    val mediaPontos: Double? = null
) {
    val textoMelhorTempo: String
        get() = melhorTempo?.let { formatarTempo(it) } ?: "-"

    val textoMediaPontos: String
        get() = mediaPontos?.let { "%.0f".format(it) } ?: "-"
}

data class EstatisticasUiState(
    val totalPartidas: Int = 0,
    val vitorias: Int = 0,
    val sequenciaAtual: Int = 0,
    val porNivel: List<EstatisticaNivel> = Nivel.entries.map { EstatisticaNivel(it) },
    val erro: String? = null
) {
    val percentualVitorias: Int
        get() = if (totalPartidas == 0) 0 else vitorias * 100 / totalPartidas
}
