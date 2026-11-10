package com.example.sudoku.data.repository

import com.example.sudoku.data.local.Conquista
import com.example.sudoku.data.local.ConquistaDAO
import com.example.sudoku.data.local.Partida
import com.example.sudoku.data.local.PartidaDAO
import com.example.sudoku.logic.RegraConquista
import com.example.sudoku.logic.RegrasConquistas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ConquistaRepository(
    private val conquistaDao: ConquistaDAO,
    private val partidaDao: PartidaDAO
) {

    fun getAllConquistas(): Flow<Resultado<List<Conquista>>> =
        conquistaDao.buscarTodos()
            .map<List<Conquista>, Resultado<List<Conquista>>> { Resultado.Sucesso(it) }
            .catch { emit(Resultado.Erro("Não foi possível carregar as conquistas")) }

    fun getAllPartidas(): Flow<Resultado<List<Partida>>> =
        partidaDao.buscarTodos()
            .map<List<Partida>, Resultado<List<Partida>>> { Resultado.Sucesso(it) }
            .catch { emit(Resultado.Erro("Não foi possível carregar as partidas")) }

    fun existeNovaNaoVista(): Flow<Boolean> =
        conquistaDao.buscarTodos()
            .map { conquistas -> conquistas.any { !it.vista } }
            .catch { emit(false) }

    suspend fun verificar(): Resultado<List<RegraConquista>> =
        try {
            val partidas = partidaDao.buscarTodosAgora()
            val codigosDesbloqueados = conquistaDao.buscarTodosAgora().map { it.codigo }.toSet()
            val novas = RegrasConquistas.novasConquistas(partidas, codigosDesbloqueados)
            novas.forEach { regra -> conquistaDao.inserir(Conquista(codigo = regra.codigo)) }
            Resultado.Sucesso(novas)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível verificar as conquistas")
        }

    suspend fun marcarComoVistas(): Resultado<Unit> =
        try {
            conquistaDao.buscarTodosAgora()
                .filter { !it.vista }
                .forEach { conquistaDao.atualizar(it.copy(vista = true)) }
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível atualizar as conquistas")
        }

    suspend fun resetar(): Resultado<Unit> =
        try {
            conquistaDao.deletarTodos()
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível resetar as conquistas")
        }
}
