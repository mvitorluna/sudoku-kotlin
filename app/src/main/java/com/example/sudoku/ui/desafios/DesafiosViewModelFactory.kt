package com.example.sudoku.ui.desafios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sudoku.data.repository.DesafioRepository

class DesafiosViewModelFactory(
    private val repository: DesafioRepository,
    private val apenasAtivos: Boolean = true
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DesafiosViewModel::class.java)) {
            return DesafiosViewModel(repository, apenasAtivos) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
