package com.example.sudoku.logic

import com.example.sudoku.data.local.Partida

data class RegraConquista(
    val codigo: String,
    val titulo: String,
    val descricao: String,
    val meta: Int,
    val progresso: (List<Partida>) -> Int
) {
    fun progressoAtual(partidas: List<Partida>): Int = progresso(partidas).coerceAtMost(meta)

    fun foiAlcancada(partidas: List<Partida>): Boolean = progresso(partidas) >= meta
}

object RegrasConquistas {

    val todas: List<RegraConquista> = listOf(
        RegraConquista("PRIMEIRA_VITORIA", "Primeira vitória", "Vença uma partida", 1) { partidas ->
            partidas.count { it.venceu }
        },
        RegraConquista("PERFEITO", "Perfeito", "Vença uma partida sem errar", 1) { partidas ->
            partidas.count { it.venceu && it.erros == 0 }
        },
        RegraConquista("SEM_AJUDA", "Sem ajuda", "Vença uma partida sem usar dica", 1) { partidas ->
            partidas.count { it.venceu && it.dicasUsadas == 0 }
        },
        RegraConquista("RELAMPAGO", "Relâmpago", "Vença um Fácil em menos de 60 segundos", 1) { partidas ->
            partidas.count { it.venceu && it.nivel == "FACIL" && it.tempoSegundos < 60 }
        },
        RegraConquista("MESTRE_9X9", "Mestre 9x9", "Vença uma partida no Difícil", 1) { partidas ->
            partidas.count { it.venceu && it.nivel == "DIFICIL" }
        },
        RegraConquista("EXPLORADOR", "Explorador", "Vença nos 3 níveis", 3) { partidas ->
            partidas.filter { it.venceu }.map { it.nivel }.distinct().size
        },
        RegraConquista("MARATONA", "Maratona", "Jogue 10 partidas", 10) { partidas ->
            partidas.size
        },
        RegraConquista("EM_CHAMAS", "Em chamas", "Vença 3 partidas seguidas", 3) { partidas ->
            partidas.sortedByDescending { it.data }.takeWhile { it.venceu }.size
        },
        RegraConquista("DESAFIANTE", "Desafiante", "Vença um desafio do admin", 1) { partidas ->
            partidas.count { it.venceu && it.semente != 0L }
        },
        RegraConquista("COLECIONADOR", "Colecionador", "Some 10.000 pontos", 10_000) { partidas ->
            partidas.sumOf { it.pontos }
        }
    )

    fun buscarPorCodigo(codigo: String): RegraConquista? =
        todas.firstOrNull { it.codigo == codigo }

    fun novasConquistas(partidas: List<Partida>, codigosDesbloqueados: Set<String>): List<RegraConquista> =
        todas
            .filter { it.codigo !in codigosDesbloqueados }
            .filter { it.foiAlcancada(partidas) }
}
