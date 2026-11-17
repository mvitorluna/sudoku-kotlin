package com.example.sudoku.data.local

// ╔══════════════════════════════════════════════╗
// ║  PartidaDAO.kt                               ║
// ║  DAO: consultas da tabela partidas           ║
// ║  inserir · buscar · atualizar · deletar      ║
// ╚══════════════════════════════════════════════╝

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDAO {

    @Insert
    suspend fun inserir(partida: Partida)

    @Query("SELECT * FROM partidas ORDER BY data DESC")
    fun buscarTodos(): Flow<List<Partida>>

    @Query("SELECT * FROM partidas ORDER BY data DESC")
    suspend fun buscarTodosAgora(): List<Partida>

    @Update
    suspend fun atualizar(partida: Partida)

    @Delete
    suspend fun deletar(partida: Partida)

    @Query("DELETE FROM partidas")
    suspend fun deletarTodos()
}
