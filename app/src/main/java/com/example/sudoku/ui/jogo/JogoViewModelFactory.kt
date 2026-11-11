package com.example.sudoku.ui.jogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.data.repository.ConquistaRepository
import com.example.sudoku.data.repository.PartidaRepository
import com.example.sudoku.data.repository.RankingRepository
import com.example.sudoku.logic.Nivel

// Além dos Repositories, recebe o nível e a semente que chegam pela rota jogo/{nivel}/{semente}
class JogoViewModelFactory(
    private val nivel: Nivel,
    private val semente: Long,
    private val partidaRepository: PartidaRepository,
    private val conquistaRepository: ConquistaRepository,
    private val rankingRepository: RankingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JogoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JogoViewModel(
                nivel,
                semente,
                partidaRepository,
                conquistaRepository,
                rankingRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
