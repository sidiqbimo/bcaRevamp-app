package com.synrgyseveneight.bcarevamp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synrgyseveneight.bcarevamp.data.model.AccountData
import com.synrgyseveneight.bcarevamp.data.model.SearchAccountResponse
import com.synrgyseveneight.bcarevamp.data.network.ApiService
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class TransferViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitClient.getInstance().create(ApiService::class.java)
    val accountData = MutableLiveData<AccountData>()
    val senderAccountData = MutableLiveData<AccountData>()
    val error = MutableLiveData<String>()

    //destination account
    fun searchAccount(token: String,accountNumber: String) {
        viewModelScope.launch {
            val response = apiService.searchAccount("Bearer $token", accountNumber)
            Log.d("TransferViewModel", "Token for SEARCHACCOUNT : $token with accountNumber : $accountNumber")
            if (response.isSuccessful && response.body()?.status == true) {
                Log.d("TransferViewModel", "Response Body: ${response.body()}")
                accountData.value = response.body()?.data
            } else {
                error.value = response.body()?.message ?: "Terjadi kesalahan. Mohon coba kembali"
                Log.d("TransferViewModel", "Error Body: ${response.errorBody()?.string()}")
            }
        }
    }

    //sender account
    fun searchSenderAccount(token: String, accountNumber: String) {
        viewModelScope.launch {
            val response = apiService.searchAccount("Bearer $token", accountNumber)
            Log.d("TransferViewModel", "Token for SEARCHACCOUNT SENDER : $token with accountNumber : $accountNumber")
            if (response.isSuccessful && response.body()?.status == true) {
                Log.d("TransferViewModel", "Response Body for SENDER: ${response.body()}")
                senderAccountData.value = response.body()?.data
            } else {
                error.value = response.body()?.message ?: "Terjadi kesalahan. Mohon coba kembali"
                Log.d("TransferViewModel", "Error Body: ${response.errorBody()?.string()}")
            }
        }
    }

}
