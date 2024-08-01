package com.synrgyseveneight.bcarevamp.data.network

import retrofit2.Call
import com.synrgyseveneight.bcarevamp.data.model.AuthRequest
import com.synrgyseveneight.bcarevamp.data.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Endpoint untuk sign-in
    @POST("/api/v1/auth/sign-in")
    fun signIn(@Body request: AuthRequest): Call<AuthResponse>
}