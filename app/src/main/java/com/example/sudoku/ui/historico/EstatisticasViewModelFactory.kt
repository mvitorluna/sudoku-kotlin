package com.example.sudoku.ui.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.data.repository.PartidaRepository

class EstatisticasViewModelFactory(
    private val repository: PartidaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstatisticasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstatisticasViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
