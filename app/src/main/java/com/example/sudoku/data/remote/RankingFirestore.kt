package com.example.sudoku.data.remote

import com.example.sudoku.data.local.RankingDao
import com.example.sudoku.data.local.RankingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RankingFirestore(private val dao: RankingDao) {

    suspend fun inicializarRankingPadrao() {
        try {
            if (dao.contar() == 0) {
                val exemplos = listOf(
                    RankingEntity(UUID.randomUUID().toString(), "Carlos Silva", "FACIL", 1250, 45, System.currentTimeMillis() - 86400000),
                    RankingEntity(UUID.randomUUID().toString(), "Ana Souza", "FACIL", 1100, 60, System.currentTimeMillis() - 43200000),
                    RankingEntity(UUID.randomUUID().toString(), "Gabriel Admin", "MEDIO", 2400, 120, System.currentTimeMillis() - 72000000),
                    RankingEntity(UUID.randomUUID().toString(), "Mariana", "MEDIO", 2150, 150, System.currentTimeMillis() - 36000000),
                    RankingEntity(UUID.randomUUID().toString(), "Eduardo", "DIFICIL", 3800, 240, System.currentTimeMillis() - 100000000),
                    RankingEntity(UUID.randomUUID().toString(), "Arthur", "DIFICIL", 3500, 310, System.currentTimeMillis() - 15000000)
                )
                exemplos.forEach { dao.inserir(it) }
            }
        } catch (_: Exception) {}
    }

    fun buscarTodos(): Flow<List<RankingRemoto>> {
        return dao.buscarTodos().map { entities ->
            entities.map {
                RankingRemoto(
                    id = it.id,
                    nomeJogador = it.nomeJogador,
                    nivel = it.nivel,
                    pontos = it.pontos,
                    tempoSegundos = it.tempoSegundos,
                    data = it.data
                )
            }
        }
    }

    suspend fun inserir(ranking: RankingRemoto) {
        val entity = RankingEntity(
            id = ranking.id.ifEmpty { UUID.randomUUID().toString() },
            nomeJogador = ranking.nomeJogador,
            nivel = ranking.nivel,
            pontos = ranking.pontos,
            tempoSegundos = ranking.tempoSegundos,
            data = ranking.data
        )
        dao.inserir(entity)
    }

    suspend fun deletar(id: String) {
        dao.deletar(id)
    }
}
