package com.example.sudoku.ui.ranking

// ╔══════════════════════════════════════════════╗
// ║  AdminRankingViewModelFactory.kt             ║
// ║  Factory do AdminRankingViewModel            ║
// ║  Entrega o Repository ao ViewModel           ║
// ╚══════════════════════════════════════════════╝

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.data.repository.RankingRepository

class AdminRankingViewModelFactory(
    private val repository: RankingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminRankingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminRankingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
