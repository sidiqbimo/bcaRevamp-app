package com.synrgyseveneight.bcarevamp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synrgyseveneight.bcarevamp.data.repository.MonthlyReportRepository
import kotlinx.coroutines.launch

enum class ErrorType{
    ERROR_404,
    ERROR_500,
    UNKNOWN_ERROR
}


class MonthlyReportViewModel(private val repository: MonthlyReportRepository) : ViewModel() {
    private val _income = MutableLiveData<Int>()
    val income: LiveData<Int> get() = _income

    private val _expense = MutableLiveData<Int>()
    val expense: LiveData<Int> get() = _expense

    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> get() = _total

    private val _navigationEvent = MutableLiveData<ErrorType>()
    val navigationEvent: LiveData<ErrorType> get() = _navigationEvent

    fun getMonthlyReport(month: String, year: String, token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMonthlyReport(month, year, token)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    _income.value = data?.income
                    _expense.value = data?.expense
                    _total.value = data?.total
                } else {
                    // Handle error
                    when (response.code()){
                        404 -> _navigationEvent.value = ErrorType.ERROR_404
                        500 -> _navigationEvent.value = ErrorType.ERROR_500
                        else -> {
                            //handle other error
                            _navigationEvent.value = ErrorType.UNKNOWN_ERROR
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exception
                _navigationEvent.value = ErrorType.UNKNOWN_ERROR
            }
        }
    }
}
