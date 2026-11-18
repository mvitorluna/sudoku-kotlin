package com.example.sudoku.ui.ranking

import com.example.sudoku.data.remote.RankingRemoto
import com.example.sudoku.logic.Nivel
import java.util.Locale

data class EstatisticaRankingNivel(
    val nivel: Nivel,
    val totalRegistros: Int,
    val mediaPontos: Double,
    val melhorTempo: Int?,
    val maisFrequente: String?
) {
    val textoMediaPontos: String
        get() = String.format(Locale.forLanguageTag("pt-BR"), "%.1f pts", mediaPontos)

    val textoMelhorTempo: String
        get() = if (melhorTempo != null) "$melhorTempo s" else "-"
}

data class AdminRankingUiState(
    val listaRanking: List<RankingRemoto> = emptyList(),
    val estatisticasPorNivel: List<EstatisticaRankingNivel> = emptyList(),
    val filtroNivel: Nivel? = null,
    val buscaNome: String = "",
    val confirmandoExclusao: RankingRemoto? = null,
    val carregando: Boolean = true,
    val erro: String? = null
)
