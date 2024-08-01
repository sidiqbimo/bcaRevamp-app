package com.synrgyseveneight.bcarevamp.viewmodel

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synrgyseveneight.bcarevamp.data.model.BalanceResponse
import com.synrgyseveneight.bcarevamp.data.model.Account
import com.synrgyseveneight.bcarevamp.data.model.SignInRequest
import com.synrgyseveneight.bcarevamp.data.model.SignInResponse
import com.synrgyseveneight.bcarevamp.data.network.ApiService
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> = _balance

    private val _userDetails = MutableLiveData<Account>()
    val userDetails: LiveData<Account> = _userDetails

    val apiService: ApiService = RetrofitClient.getInstance().create(ApiService::class.java)

    // Temporary method to sign in and fetch user details and token
    fun signInAndFetchDetails(context: Context, signature: String, password: String) {
        val signInRequest = SignInRequest(signature, password)
        viewModelScope.launch {
            apiService.signIn(signInRequest).enqueue(object : Callback<SignInResponse> {
                override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val token = it.data.token
                            _userDetails.value = it.data.user
                            getBalance(context,"Bearer $token")
                        }
                    } else {
                        Toast.makeText(context, "Failed to sign in: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                    Toast.makeText(context, "Failed to sign in: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun getBalance(context: Context, token: String) {
        apiService.getBalance(token).enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(call: Call<BalanceResponse>, response: Response<BalanceResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _balance.value = it.data.balance
                    }
                } else {
                    Toast.makeText(context, "Failed to retrieve balance: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                // TODO:Handle failure
                Toast.makeText(context, "Failed to retrieve balance: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}