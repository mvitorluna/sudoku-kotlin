package com.example.sudoku.ui.novojogo

// ╔══════════════════════════════════════════════╗
// ║  NovoJogoViewModelFactory.kt                 ║
// ║  Factory do NovoJogoViewModel                ║
// ║  Entrega o Repository ao ViewModel           ║
// ╚══════════════════════════════════════════════╝

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.data.repository.PartidaRepository

class NovoJogoViewModelFactory(
    private val repository: PartidaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NovoJogoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NovoJogoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
