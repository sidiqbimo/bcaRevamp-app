package com.synrgyseveneight.bcarevamp.data.network

import retrofit2.Call
import com.synrgyseveneight.bcarevamp.data.model.AuthRequest
import com.synrgyseveneight.bcarevamp.data.model.AuthResponse
import com.synrgyseveneight.bcarevamp.data.model.BalanceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    // Endpoint untuk sign-in
    @POST("/api/v1/auth/sign-in")
    fun signIn(@Body request: AuthRequest): Call<AuthResponse>

    //    Info Saldo
    @GET("api/v1/user/getBalance")
    suspend fun getBalance(@Header("Authorization") token: String): Response<BalanceResponse>
}
