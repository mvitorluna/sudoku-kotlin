package com.example.sudoku.ui.ranking

// ╔══════════════════════════════════════════════╗
// ║  RankingViewModelFactory.kt                  ║
// ║  Factory do RankingViewModel                 ║
// ║  Entrega o Repository ao ViewModel           ║
// ╚══════════════════════════════════════════════╝

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.data.repository.RankingRepository

class RankingViewModelFactory(
    private val repository: RankingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RankingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
