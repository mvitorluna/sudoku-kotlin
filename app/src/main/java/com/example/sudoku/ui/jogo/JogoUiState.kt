package com.example.sudoku.ui.jogo

import com.example.sudoku.logic.Nivel
import com.example.sudoku.logic.RegraConquista

// Máquina de estados da partida: começa EM_ANDAMENTO e termina em VENCEU ou PERDEU
enum class StatusJogo {
    EM_ANDAMENTO,
    VENCEU,
    PERDEU
}

data class JogoUiState(
    val nivel: Nivel = Nivel.FACIL,
    val semente: Long = 0,
    val tabuleiro: List<List<Int>> = emptyList(),
    val fixas: Set<Pair<Int, Int>> = emptySet(),
    val conflitos: Set<Pair<Int, Int>> = emptySet(),
    val selecionada: Pair<Int, Int>? = null,
    val erros: Int = 0,
    val dicasRestantes: Int = 0,
    val segundos: Int = 0,
    val status: StatusJogo = StatusJogo.EM_ANDAMENTO,
    val pontos: Int = 0,
    val nomeParaRanking: String = "",
    val publicado: Boolean = false,
    val conquistasNovas: List<RegraConquista> = emptyList(),
    val confirmandoSaida: Boolean = false,
    val erro: String? = null
) {
    val tempoFormatado: String
        get() = "%02d:%02d".format(segundos / 60, segundos % 60)

    val acabou: Boolean
        get() = status != StatusJogo.EM_ANDAMENTO

    val casasRestantes: Int
        get() = tabuleiro.sumOf { linha -> linha.count { it == 0 } }

    val dicasUsadas: Int
        get() = nivel.dicas - dicasRestantes

    val titulo: String
        get() = "${nivel.titulo} ${nivel.tamanho}x${nivel.tamanho}"

    val podePublicar: Boolean
        get() = status == StatusJogo.VENCEU && !publicado
}
