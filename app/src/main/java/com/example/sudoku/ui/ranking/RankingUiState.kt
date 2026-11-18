package com.example.sudoku.ui.ranking

import com.example.sudoku.data.remote.RankingRemoto
import com.example.sudoku.logic.Nivel

data class RankingItemUi(
    val posicao: Int,
    val item: RankingRemoto
) {
    val medalha: String?
        get() = when (posicao) {
            1 -> "🥇"
            2 -> "🥈"
            3 -> "🥉"
            else -> null
        }
}

data class RankingUiState(
    val itensRanking: List<RankingItemUi> = emptyList(),
    val filtroNivel: Nivel? = null,
    val buscaNome: String = "",
    val carregando: Boolean = true,
    val erro: String? = null
)
