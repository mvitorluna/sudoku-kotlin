package com.example.sudoku.ui.conquistas

import com.example.sudoku.logic.RegraConquista
import com.example.sudoku.logic.RegrasConquistas

enum class FiltroConquista(val titulo: String) {
    TODAS("Todas"),
    DESBLOQUEADAS("Desbloqueadas"),
    BLOQUEADAS("Bloqueadas")
}

data class ConquistaItem(
    val regra: RegraConquista,
    val progresso: Int,
    val desbloqueadaEm: Long?
) {
    val desbloqueada: Boolean
        get() = desbloqueadaEm != null

    val percentual: Float
        get() = progresso.toFloat() / regra.meta

    val textoProgresso: String
        get() = "$progresso/${regra.meta}"
}

data class ConquistasUiState(
    val listaDeConquistas: List<ConquistaItem> = emptyList(),
    val totalDesbloqueadas: Int = 0,
    val filtro: FiltroConquista = FiltroConquista.TODAS,
    val confirmandoReset: Boolean = false,
    val temNovaNaoVista: Boolean = false,
    val erro: String? = null
) {
    val textoContador: String
        get() = "$totalDesbloqueadas de ${RegrasConquistas.todas.size}"

    val textoListaVazia: String
        get() = when (filtro) {
            FiltroConquista.DESBLOQUEADAS -> "Nenhuma conquista desbloqueada ainda"
            FiltroConquista.BLOQUEADAS -> "Você desbloqueou todas as conquistas!"
            FiltroConquista.TODAS -> "Nenhuma conquista encontrada"
        }
}
