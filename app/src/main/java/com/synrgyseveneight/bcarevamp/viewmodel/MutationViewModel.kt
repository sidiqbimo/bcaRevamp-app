package com.synrgyseveneight.bcarevamp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synrgyseveneight.bcarevamp.data.model.MutationData
import com.synrgyseveneight.bcarevamp.data.model.MutationRequest
import com.synrgyseveneight.bcarevamp.data.repository.MutationRepository
import kotlinx.coroutines.launch

class MutationViewModel(private val repository: MutationRepository) : ViewModel() {

    private val _mutationData = MutableLiveData<List<MutationData>>()
    val mutationData: LiveData<List<MutationData>> get() = _mutationData

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
                    _mutationData.value = response.body()?.data ?: emptyList()
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
