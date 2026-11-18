package com.example.sudoku.ui.historico

import com.example.sudoku.data.local.Partida
import com.example.sudoku.logic.Nivel

enum class OrdenacaoPartidas(val titulo: String) {
    MAIS_RECENTE("Mais recente"),
    MENOR_TEMPO("Menor tempo"),
    MAIS_PONTOS("Mais pontos")
}

data class HistoricoUiState(
    val listaDePartidas: List<Partida> = emptyList(),
    val filtroNivel: Nivel? = null,
    val soVitorias: Boolean = false,
    val soFavoritas: Boolean = false,
    val soDesafios: Boolean = false,
    val ordenacao: OrdenacaoPartidas = OrdenacaoPartidas.MAIS_RECENTE,
    val partidaExpandidaId: Int? = null,
    val confirmandoLimpar: Boolean = false,
    val erro: String? = null
) {
    val temFiltroAtivo: Boolean
        get() = filtroNivel != null || soVitorias || soFavoritas || soDesafios

    val textoListaVazia: String
        get() = when {
            filtroNivel != null -> "Nenhuma partida no nível ${filtroNivel.titulo}"
            temFiltroAtivo -> "Nenhuma partida com esses filtros"
            else -> "Você ainda não jogou nenhuma partida"
        }
}

fun formatarTempo(segundos: Int): String = "%02d:%02d".format(segundos / 60, segundos % 60)
