package com.example.sudoku.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConquistaDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(conquista: Conquista)

    @Query("SELECT * FROM conquistas ORDER BY desbloqueadaEm DESC")
    fun buscarTodos(): Flow<List<Conquista>>

    @Query("SELECT * FROM conquistas")
    suspend fun buscarTodosAgora(): List<Conquista>

    @Update
    suspend fun atualizar(conquista: Conquista)

    @Query("DELETE FROM conquistas")
    suspend fun deletarTodos()
}
