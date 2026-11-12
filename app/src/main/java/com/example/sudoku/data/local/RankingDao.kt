package com.example.sudoku.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingDao {
    @Query("SELECT * FROM ranking")
    fun buscarTodos(): Flow<List<RankingEntity>>

    @Query("SELECT * FROM ranking")
    suspend fun buscarTodosAgora(): List<RankingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(ranking: RankingEntity)

    @Query("DELETE FROM ranking WHERE id = :id")
    suspend fun deletar(id: String)

    @Query("SELECT COUNT(*) FROM ranking")
    suspend fun contar(): Int
}
