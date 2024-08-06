package com.synrgyseveneight.bcarevamp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synrgyseveneight.bcarevamp.data.model.AccountData
import com.synrgyseveneight.bcarevamp.data.model.SingleLiveEvent
import com.synrgyseveneight.bcarevamp.data.model.TransferRequest
import com.synrgyseveneight.bcarevamp.data.model.TransferResponse
import com.synrgyseveneight.bcarevamp.data.network.ApiService
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.SocketTimeoutException

class TransferViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitClient.getInstance().create(ApiService::class.java)
    val accountData = MutableLiveData<AccountData>()
    val senderAccountData = MutableLiveData<AccountData>()
    val error = MutableLiveData<String>()

    // Error maks 3 kali - initiate
    private val _attemptCount = MutableLiveData<Int>()
    val attemptCount: LiveData<Int> get() = _attemptCount

    private val _logoutEvent = SingleLiveEvent<Unit>()
    val logoutEvent: LiveData<Unit> get() = _logoutEvent

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        _attemptCount.value = 0
    }

    /*
    fun verifyPin(token: String, pin: String) {
        viewModelScope.launch {
            try {
                val response = repository.verifyPin(token, pin)
                if (response.isSuccessful) {
                    // Handle successful response
                    _attemptCount.value = 0
                } else {
                    handlePinFailure()
                }
            } catch (e: Exception) {
                handlePinFailure()
            }
        }
    }

    */

    /*
    private fun handlePinFailure() {
        _attemptCount.value = _attemptCount.value?.plus(1)
        if (_attemptCount.value ?: 0 >= 3) {
            repository.clearToken()
            _logoutEvent.postValue(Unit)
        }
    }
    */


    //destination account
    fun searchAccount(token: String,accountNumber: String) {
        viewModelScope.launch {
            val response = apiService.searchAccount("Bearer $token", accountNumber)
            Log.d("TransferViewModel", "Token for SEARCHACCOUNT : $token with accountNumber : $accountNumber")
            if (response.isSuccessful && response.body()?.status == true) {
                Log.d("TransferViewModel", "Response Body: ${response.body()}")
                accountData.value = response.body()?.data
            } else {
                error.value = response.body()?.message ?: "Nomor rekening tidak ditemukan"
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

    //Transfer Begin
    suspend fun performTransfer(
        token: String,
        destinationAccountNumber: String,
        amount: Int,
        mpin: String,
        note: String,
        savedAccount: Boolean
    ): Response<TransferResponse>? {
        val request = TransferRequest(destinationAccountNumber, amount, mpin, note, savedAccount)
        Log.d("TransferViewModel", "Performing Transfer with Token: $token")
        Log.d("TransferViewModel", "Performing Transfer Request Body: $request")
        val response = apiService.transfer("Bearer $token", request)
        Log.d("TransferViewModel", "Performing Transfer Response Body: $response")

        // biar kalau timeout, ga langsung crash
        return try {
            val response = apiService.transfer("Bearer $token", request)
            Log.d("TransferViewModel", "Performing Transfer Response Body: $response")
            response
        } catch (e: SocketTimeoutException) {
            Log.e("TransferViewModel", "Request timed out", e)
            null
        }
    }

}
