package com.synrgyseveneight.bcarevamp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.ui.info.ApiService
import com.synrgyseveneight.bcarevamp.ui.info.BalanceResponse
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import kotlin.reflect.KProperty

private val RetrofitClient.Companion.instance: Unit
    get() {}

class InfoViewModel : ViewModel() {
    private val _balance = MutableLiveData<String>()
    val balance: LiveData<String> get() = _balance

    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    fun fetchBalance() {
        apiService.getBalance().enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(call: Call<BalanceResponse>, response: Response<BalanceResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { balanceResponse ->
                        if (balanceResponse.status) {
                            val balance = balanceResponse.data.balance
                            _balance.value = "Rp$balance"
                        } else {
                            _balance.value = "Failed to get Balance"
                        }
                    }
                } else {
                    _balance.value = "Failed to get Balance"
                }
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                _balance.value = "Error: ${t.message}"
            }
        })
    }
}

public operator fun <T> Lazy<T>.getValue(
    infoViewModel: InfoViewModel,
    property: KProperty<T?>
): ApiService {
    return value as ApiService
}
