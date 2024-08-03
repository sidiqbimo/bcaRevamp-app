package com.synrgyseveneight.bcarevamp.ui.info

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/v1/user/getBalance")
    fun getBalance(): Call<BalanceResponse>

}