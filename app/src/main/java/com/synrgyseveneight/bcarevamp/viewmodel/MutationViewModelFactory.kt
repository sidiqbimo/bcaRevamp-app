package com.synrgyseveneight.bcarevamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synrgyseveneight.bcarevamp.data.repository.MutationRepository

class MutationViewModelFactory(private val repository: MutationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MutationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MutationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

