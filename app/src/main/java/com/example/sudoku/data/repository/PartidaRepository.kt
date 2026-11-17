package com.example.sudoku.data.repository

// ╔══════════════════════════════════════════════╗
// ║  PartidaRepository.kt                        ║
// ║  Repository das partidas                     ║
// ║  Recebe o DAO e trata as exceções            ║
// ╚══════════════════════════════════════════════╝

import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.local.PartidaDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PartidaRepository(private val partidaDao: PartidaDAO) {

    fun getAllPartidas(): Flow<Resultado<List<Partida>>> =
        partidaDao.buscarTodos()
            .map<List<Partida>, Resultado<List<Partida>>> { Resultado.Sucesso(it) }
            .catch { emit(Resultado.Erro("Não foi possível carregar o histórico")) }

    suspend fun inserir(partida: Partida): Resultado<Unit> =
        try {
            partidaDao.inserir(partida)
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível salvar a partida")
        }

    suspend fun atualizar(partida: Partida): Resultado<Unit> =
        try {
            partidaDao.atualizar(partida)
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível atualizar a partida")
        }

    suspend fun deletar(partida: Partida): Resultado<Unit> =
        try {
            partidaDao.deletar(partida)
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível excluir a partida")
        }

    suspend fun deletarTodos(): Resultado<Unit> =
        try {
            partidaDao.deletarTodos()
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível limpar o histórico")
        }
}
