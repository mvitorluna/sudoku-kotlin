package com.example.sudoku.ui.navegacao

// ╔══════════════════════════════════════════════╗
// ║  Rotas.kt                                    ║
// ║  Constantes das ROTAS de navegação           ║
// ║  Centraliza os nomes para evitar typos       ║
// ╚══════════════════════════════════════════════╝

object Rotas {
    const val HOME    = "home"
    const val JOGADOR = "jogador"
    const val ADMIN   = "admin"

    // Rotas com argumento: "jogo/{nivel}/{semente}" e "desafio_jogo/{desafioId}/{semente}"
    const val JOGO         = "jogo/{nivel}/{semente}"
    const val DESAFIO_JOGO = "desafio_jogo/{desafioId}/{semente}"

    // Funções auxiliares para montar a rota com o valor real
    fun jogo(nivel: String, semente: Long) = "jogo/$nivel/$semente"
    fun desafioJogo(desafioId: String, semente: Long) = "desafio_jogo/$desafioId/$semente"
}

// Rotas das abas internas (usadas DENTRO do NavHost da TelaJogador e da TelaAdmin)
object RotasAbas {
    // Abas do jogador
    const val ABA_JOGAR      = "aba_jogar"
    const val ABA_DESAFIOS   = "aba_desafios"
    const val ABA_HISTORICO  = "aba_historico"
    const val ABA_RANKING    = "aba_ranking"
    const val ABA_CONQUISTAS = "aba_conquistas"

    // Abas do admin
    const val ABA_ADMIN_DESAFIOS = "aba_admin_desafios"
    const val ABA_ADMIN_RANKING  = "aba_admin_ranking"
}
