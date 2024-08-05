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
    val error = MutableLiveData<String>()

    fun searchAccount(token: String,accountNumber: String) {
        viewModelScope.launch {
            val response = apiService.searchAccount("Bearer $token", accountNumber)
            if (response.isSuccessful && response.body()?.status == true) {
                Log.d("TransferViewModel", "Response Body: ${response.body()}")
                accountData.value = response.body()?.data
            } else {
                error.value = response.body()?.message ?: "Terjadi kesalahan. Mohon coba kembali"
                Log.d("TransferViewModel", "Error Body: ${response.errorBody()?.string()}")
            }
            /*
            try {
                val response = apiService.searchAccount("mytoken", accountNumber)
                if (response.isSuccessful && response.body()?.status == true) {
                    Log.d("TransferViewModel", "Response Body: ${response.body()}")
                    accountData.value = response.body()?.data
                } else {
                    error.value = response.body()?.message ?: "Terjadi kesalahan. Mohon coba kembali"
                    Log.d("TransferViewModel", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                error.value = e.message
            } */
        }

        /*
        apiService.searchAccount(accountNumber).enqueue(object : Callback<SearchAccountResponse> {
            override fun onResponse(call: Call<SearchAccountResponse>, response: Response<SearchAccountResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    accountData.value = response.body()?.data
                } else {
                    error.value = response.body()?.message ?: "Unknown error"
                }
            }

            override fun onFailure(call: Call<SearchAccountResponse>, t: Throwable) {
                error.value = t.message
            }
        }) */
    }

}
