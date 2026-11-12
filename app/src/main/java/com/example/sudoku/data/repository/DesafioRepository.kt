package com.example.sudoku.data.repository

import com.example.sudoku.data.local.DesafioDAO
import com.example.sudoku.data.local.DesafioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DesafioRepository(private val desafioDao: DesafioDAO) {

    suspend fun inicializarDesafiosPadrao() {
        try {
            if (desafioDao.contar() == 0) {
                val padroes = listOf(
                    DesafioEntity(
                        id = "teste_4x4",
                        titulo = "Teste 4x4 (10 min)",
                        descricao = "Sudoku 4x4 super fácil com limite de 10 minutos para testes.",
                        tipo = "CRONOMETRO",
                        tempoLimiteSegundos = 600,
                        casasVazias = 4,
                        dicas = 3,
                        maxErros = 3,
                        ativo = true,
                        ordem = 0,
                        tamanho = 4
                    ),
                    DesafioEntity(
                        id = "cronometro_3m",
                        titulo = "Cronômetro 3 Minutos",
                        descricao = "Sudoku 9x9 com limite estrito de 3 minutos.",
                        tipo = "CRONOMETRO",
                        tempoLimiteSegundos = 180,
                        casasVazias = 45,
                        dicas = 1,
                        maxErros = 3,
                        ativo = true,
                        ordem = 1,
                        tamanho = 9
                    ),
                    DesafioEntity(
                        id = "cronometro_5m",
                        titulo = "Cronômetro 5 Minutos",
                        descricao = "Sudoku 9x9 com limite de 5 minutos.",
                        tipo = "CRONOMETRO",
                        tempoLimiteSegundos = 300,
                        casasVazias = 45,
                        dicas = 1,
                        maxErros = 3,
                        ativo = true,
                        ordem = 2,
                        tamanho = 9
                    ),
                    DesafioEntity(
                        id = "cronometro_10m",
                        titulo = "Cronômetro 10 Minutos",
                        descricao = "Sudoku 9x9 com limite de 10 minutos.",
                        tipo = "CRONOMETRO",
                        tempoLimiteSegundos = 600,
                        casasVazias = 45,
                        dicas = 1,
                        maxErros = 3,
                        ativo = true,
                        ordem = 3,
                        tamanho = 9
                    ),
                    DesafioEntity(
                        id = "menos_numeros",
                        titulo = "Menos Números",
                        descricao = "Sudoku 9x9 com apenas 1 número por quadrado (9 pistas iniciais).",
                        tipo = "MENOS_NUMEROS",
                        tempoLimiteSegundos = 0,
                        casasVazias = 72,
                        dicas = 1,
                        maxErros = 3,
                        ativo = true,
                        ordem = 4,
                        tamanho = 9
                    ),
                    DesafioEntity(
                        id = "sem_dicas",
                        titulo = "Sem Dicas",
                        descricao = "Sudoku 9x9 desafiador sem nenhuma dica disponível.",
                        tipo = "SEM_DICAS",
                        tempoLimiteSegundos = 0,
                        casasVazias = 45,
                        dicas = 0,
                        maxErros = 3,
                        ativo = true,
                        ordem = 5,
                        tamanho = 9
                    ),
                    DesafioEntity(
                        id = "sem_erros",
                        titulo = "Sem Erros",
                        descricao = "Sudoku 9x9 onde qualquer erro resulta em derrota imediata.",
                        tipo = "SEM_ERROS",
                        tempoLimiteSegundos = 0,
                        casasVazias = 45,
                        dicas = 1,
                        maxErros = 0,
                        ativo = true,
                        ordem = 6,
                        tamanho = 9
                    )
                )
                padroes.forEach { desafioDao.inserir(it) }
            }
        } catch (_: Exception) {
        }
    }

    fun getDesafiosAtivos(): Flow<Resultado<List<DesafioEntity>>> =
        desafioDao.buscarAtivos()
            .map<List<DesafioEntity>, Resultado<List<DesafioEntity>>> { Resultado.Sucesso(it) }
            .catch { emit(Resultado.Erro("Não foi possível carregar os desafios")) }

    fun getTodosDesafios(): Flow<Resultado<List<DesafioEntity>>> =
        desafioDao.buscarTodos()
            .map<List<DesafioEntity>, Resultado<List<DesafioEntity>>> { Resultado.Sucesso(it) }
            .catch { emit(Resultado.Erro("Não foi possível carregar os desafios")) }

    suspend fun atualizar(desafio: DesafioEntity): Resultado<Unit> =
        try {
            desafioDao.atualizar(desafio)
            Resultado.Sucesso(Unit)
        } catch (e: Exception) {
            Resultado.Erro("Não foi possível atualizar o desafio")
        }

    suspend fun buscarPorId(id: String): DesafioEntity? =
        try {
            desafioDao.buscarPorId(id)
        } catch (e: Exception) {
            null
        }

    suspend fun marcarConcluido(id: String) {
        try {
            desafioDao.marcarConcluido(id)
        } catch (_: Exception) {
        }
    }
}
