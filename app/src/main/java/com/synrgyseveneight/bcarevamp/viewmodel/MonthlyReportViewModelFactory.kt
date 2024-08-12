package com.synrgyseveneight.bcarevamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.synrgyseveneight.bcarevamp.data.repository.MonthlyReportRepository

class MonthlyReportViewModelFactory(private val repository: MonthlyReportRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MonthlyReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MonthlyReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
