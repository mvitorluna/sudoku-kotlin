package com.example.sudoku.data.repository

// PROVISÓRIO: só a assinatura de publicar da P6 (Gabriel), sem Firebase. Será substituída pela versão dele.
class RankingRepository {

    suspend fun publicar(nomeJogador: String, nivel: String, pontos: Int, tempoSegundos: Int): Resultado<Unit> =
        try {
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível publicar no ranking")
        }
}
