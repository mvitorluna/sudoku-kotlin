package com.example.sudoku.ui.conquistas

// ╔══════════════════════════════════════════════╗
// ║  ConquistasUiState.kt                        ║
// ║  Estado da tela de conquistas                ║
// ║  O que a AbaConquistas mostra                ║
// ╚══════════════════════════════════════════════╝

import com.example.sudoku.logic.RegraConquista
import com.example.sudoku.logic.RegrasConquistas
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    val textoData: String
        get() {
            if (desbloqueadaEm == null) return ""
            return SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(desbloqueadaEm))
        }
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
