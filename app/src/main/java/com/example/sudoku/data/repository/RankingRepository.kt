package com.example.sudoku.data.repository

import com.example.sudoku.data.remote.RankingRemoto
import com.example.sudoku.data.remote.RankingFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.UUID

class RankingRepository(private val rankingFirestore: RankingFirestore) {

    constructor() : this(RankingFirestore(object : com.example.sudoku.data.local.RankingDao {
        override fun buscarTodos(): Flow<List<com.example.sudoku.data.local.RankingEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override suspend fun buscarTodosAgora(): List<com.example.sudoku.data.local.RankingEntity> = emptyList()
        override suspend fun inserir(ranking: com.example.sudoku.data.local.RankingEntity) {}
        override suspend fun deletar(id: String) {}
        override suspend fun contar(): Int = 0
    }))

    suspend fun inicializarRankingPadrao() {
        rankingFirestore.inicializarRankingPadrao()
    }

    fun getRanking(): Flow<Resultado<List<RankingRemoto>>> =
        rankingFirestore.buscarTodos()
            .map<List<RankingRemoto>, Resultado<List<RankingRemoto>>> { Resultado.Sucesso(it) }
            .catch { emit(Resultado.Erro("Não foi possível carregar o ranking. Verifique sua conexão.")) }

    suspend fun publicar(nomeJogador: String, nivel: String, pontos: Int, tempoSegundos: Int): Resultado<Unit> =
        try {
            val novo = RankingRemoto(
                id = UUID.randomUUID().toString(),
                nomeJogador = nomeJogador,
                nivel = nivel,
                pontos = pontos,
                tempoSegundos = tempoSegundos,
                data = System.currentTimeMillis()
            )
            rankingFirestore.inserir(novo)
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível publicar no ranking. Tente novamente.")
        }

    suspend fun deletar(id: String): Resultado<Unit> =
        try {
            rankingFirestore.deletar(id)
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível excluir o item do ranking.")
        }
}
