package com.example.sudoku.ui.conquistas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.data.repository.ConquistaRepository

class ConquistasViewModelFactory(
    private val repository: ConquistaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConquistasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConquistasViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
