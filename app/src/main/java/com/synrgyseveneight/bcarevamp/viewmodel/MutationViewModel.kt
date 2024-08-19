package com.synrgyseveneight.bcarevamp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synrgyseveneight.bcarevamp.data.model.MutationResponseData
import com.synrgyseveneight.bcarevamp.data.model.MutationRequest
import com.synrgyseveneight.bcarevamp.data.repository.MutationRepository
import kotlinx.coroutines.launch

class MutationViewModel(private val repository: MutationRepository) : ViewModel() {

    private val _mutationResponseData = MutableLiveData<List<MutationResponseData>>()
    val mutationResponseData: LiveData<List<MutationResponseData>> get() = _mutationResponseData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchMutations(page: Int, size: Int, token: String, requestBody: MutationRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAllMutations(page, size, token, requestBody)
                if (response.isSuccessful) {
                    // Akses `mutation_responses` dari `data`
                    _mutationResponseData.value = response.body()?.data?.mutation_responses ?: emptyList()
                } else {
                    _error.value = response.code().toString()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
