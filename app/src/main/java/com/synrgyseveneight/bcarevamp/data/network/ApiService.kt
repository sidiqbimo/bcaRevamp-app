package com.synrgyseveneight.bcarevamp.data.network

import com.synrgyseveneight.bcarevamp.data.model.BalanceResponse
import com.synrgyseveneight.bcarevamp.data.model.SignInRequest
import com.synrgyseveneight.bcarevamp.data.model.SignInResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("user/getBalance")
    fun getBalance(@Header("Authorization") token: String): Call<BalanceResponse>

    @POST("auth/sign-in")
    fun signIn(@Body signInRequest: SignInRequest): Call<SignInResponse>
}