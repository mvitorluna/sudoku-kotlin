package com.example.sudoku.ui.navegacao

object Rotas {
    const val HOME    = "home"
    const val JOGADOR = "jogador"
    const val ADMIN   = "admin"

    const val JOGO         = "jogo/{nivel}/{semente}"
    const val DESAFIO_JOGO = "desafio_jogo/{desafioId}/{semente}"

    fun jogo(nivel: String, semente: Long) = "jogo/$nivel/$semente"
    fun desafioJogo(desafioId: String, semente: Long) = "desafio_jogo/$desafioId/$semente"
}

object RotasAbas {
    const val ABA_JOGAR      = "aba_jogar"
    const val ABA_DESAFIOS   = "aba_desafios"
    const val ABA_HISTORICO  = "aba_historico"
    const val ABA_RANKING    = "aba_ranking"
    const val ABA_CONQUISTAS = "aba_conquistas"

    const val ABA_ADMIN_DESAFIOS = "aba_admin_desafios"
    const val ABA_ADMIN_RANKING  = "aba_admin_ranking"
}
