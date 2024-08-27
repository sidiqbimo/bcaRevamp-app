package com.synrgyseveneight.bcarevamp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synrgyseveneight.bcarevamp.data.model.qris.QRISResponse
import com.synrgyseveneight.bcarevamp.data.model.qris.QRISTransferRequest
import com.synrgyseveneight.bcarevamp.data.model.qris.QrisData
import com.synrgyseveneight.bcarevamp.data.network.ApiService
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.SocketTimeoutException

class QRISViewModel : ViewModel(){
    private val apiService: ApiService = RetrofitClient.getInstance().create(ApiService::class.java)
    val qrisData = MutableLiveData<QrisData>()
    val error = MutableLiveData<String>()

    val qrisNotFoundString: MutableLiveData<String> = MutableLiveData()

    fun searchQris (token: String, qrisId: String) {
        viewModelScope.launch {
            val response = apiService.searchQris("Bearer $token", qrisId)
            Log.d("QRISViewModel", "searchQris: $response with token $token and qrisId $qrisId")
            if (response.isSuccessful && response.body()?.status == true) {
                Log.d("QRISViewModel", "Response Body: ${response.body()}")
                Log.d("QRISViewModel","THE RESPONSE CODE IS ${response.code()}")
                qrisData.value = response.body()?.data
            } else if (response.code() == 500){
                Log.d("QRISViewModel", "RESPONSE BODY IS 500")
                qrisNotFoundString.value = "notfound"
            }
            else {
                error.value = response.body()?.message ?: "Nomor rekening tidak ditemukan"
                Log.d("QRISViewModel", "Error Body: ${response.errorBody()?.string()}")
            }
        }
    }

    suspend fun performQrisTransfer (
        idQris:String,
        amount: Int,
        note: String,
        mpin: String,
        token: String
    ) : Response<QRISResponse>? {
        val request = QRISTransferRequest (idQris, amount, note, mpin)

        Log.d("QRISViewModel", "Performing Transfer with Token: $token")
        Log.d("QRISViewModel", "Performing Transfer Request Body: $request")
        val response = apiService.performQrisTransfer("Bearer $token", request)
        Log.d("QRISViewModel", "Performing Transfer Response Body: $response")

        // biar kalau timeout, ga langsung crash
        return try {
            val response = apiService.performQrisTransfer("Bearer $token", request)
            Log.d("QRISViewModel", "Performing Transfer Response Body: $response")
            response
        } catch (e: SocketTimeoutException) {
            Log.e("QRISViewModel", "Request timed out", e)
            null
        }

    }
}