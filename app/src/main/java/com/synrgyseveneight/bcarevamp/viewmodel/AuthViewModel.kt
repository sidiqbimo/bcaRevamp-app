package com.synrgyseveneight.bcarevamp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.*
import com.synrgyseveneight.bcarevamp.data.model.AuthRequest
import com.synrgyseveneight.bcarevamp.data.model.AuthResponse
import com.synrgyseveneight.bcarevamp.data.network.ApiService
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    // LiveData untuk tanda tangan pengguna
    val userSignature: LiveData<String?> = repository.getUserSignature().asLiveData()

    // LiveData untuk nama pengguna
    val userName: LiveData<String?> = repository.getUserName().asLiveData()

    // LiveData untuk nomor rekening pengguna
    val userAccountNumber: LiveData<String?> = repository.getUserAccountNumber().asLiveData()

    // LiveData untuk  gambar avatar
    val userImagePath: LiveData<String?> = repository.getUserImagePath().asLiveData()

    // LiveData untuk nama bank pengguna
    val userBankName: LiveData<String?> = repository.getUserBankName().asLiveData()

    // LiveData untuk token otentikasi
    val userToken: LiveData<String?> = repository.getUserToken().asLiveData()

    // LiveData untuk saldo
    val userBalance = MutableLiveData<String>()

    // LiveData untuk waktu cek saldo
    val balanceCheckTime = MutableLiveData<String>()

    // Fungsi untuk melakukan sign-in
    fun signIn(signature: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val request = AuthRequest(signature, password)
        repository.signIn(request).enqueue(object : retrofit2.Callback<AuthResponse> {
            override fun onResponse(call: retrofit2.Call<AuthResponse>, response: retrofit2.Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val authData = response.body()?.data
                    viewModelScope.launch {
                        authData?.let {
                            repository.saveUserData(it.user.signature, it.user.name, it.user.account_number, it.user.image_path, it.user.bank_name, it.token)
                        }
                        onSuccess()
                    }
                } else {

                    val errorMessage = when (response.code()) {
                        401 -> "Signature atau password salah"
                        else -> response.message()
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: retrofit2.Call<AuthResponse>, t: Throwable) {
                onError(t.message ?: "Unknown error")
            }
        })
    }

    fun fetchBalance(token: String) {
        viewModelScope.launch {
            val balanceResponse = repository.getBalance(token)
            userBalance.value = balanceResponse?.data?.balance?.let { formatBalance(it) }
            balanceCheckTime.value = balanceResponse?.data?.check_time
            // check_time or balance adalah nama variabel yang digunakan pada response body API, mereka ngelink juga ke BalanceData
        }
    }

    private fun formatBalance(balance: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
        return formatter.format(balance)
    }

    // Fungsi untuk menghapus token
    fun clearToken(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.clearToken()
            onSuccess()
        }
    }


}