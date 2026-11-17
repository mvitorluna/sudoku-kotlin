package com.example.sudoku.data.local

// ╔══════════════════════════════════════════════╗
// ║  DesafioDAO.kt                               ║
// ║  DAO: consultas da tabela desafios           ║
// ║  buscar · inserir · atualizar                ║
// ╚══════════════════════════════════════════════╝

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DesafioDAO {

    @Query("SELECT * FROM desafios ORDER BY ordem ASC")
    fun buscarTodos(): Flow<List<DesafioEntity>>

    @Query("SELECT * FROM desafios WHERE ativo = 1 ORDER BY ordem ASC")
    fun buscarAtivos(): Flow<List<DesafioEntity>>

    @Query("SELECT * FROM desafios WHERE id = :id")
    suspend fun buscarPorId(id: String): DesafioEntity?

    @Query("SELECT COUNT(*) FROM desafios")
    suspend fun contar(): Int

    @Query("UPDATE desafios SET concluido = 1 WHERE id = :id")
    suspend fun marcarConcluido(id: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(desafio: DesafioEntity)

    @Update
    suspend fun atualizar(desafio: DesafioEntity)
}
