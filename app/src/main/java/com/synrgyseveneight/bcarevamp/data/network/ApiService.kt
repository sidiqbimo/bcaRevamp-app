package com.synrgyseveneight.bcarevamp.data.network

import com.synrgyseveneight.bcarevamp.data.model.AuthRequest
import com.synrgyseveneight.bcarevamp.data.model.AuthResponse
import com.synrgyseveneight.bcarevamp.data.model.BalanceResponse
import com.synrgyseveneight.bcarevamp.data.model.MonthlyReportResponse
import com.synrgyseveneight.bcarevamp.data.model.SearchAccountResponse
import com.synrgyseveneight.bcarevamp.data.model.TransferRequest
import com.synrgyseveneight.bcarevamp.data.model.TransferResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Endpoint untuk sign-in
    @POST("/api/v1/auth/sign-in")
    fun signIn(@Body request: AuthRequest): Call<AuthResponse>

    //    Info Saldo
    @GET("api/v1/user/getBalance")
    suspend fun getBalance(@Header("Authorization") token: String): Response<BalanceResponse>

    @GET ("api/v1/user/search-no-rek/{targetAccountTransfer}")
    suspend fun searchAccount(@Header("Authorization") token: String, @Path("targetAccountTransfer") targetAccountTransfer: String): Response<SearchAccountResponse>

    @POST("api/v1/transactions/bca-transfer")
    suspend fun transfer(
        @Header("Authorization") token: String,
        @Body request: TransferRequest
    ): Response<TransferResponse>

    // Endpoint untuk mendapatkan laporan bulanan
    @GET("/api/v1/transactions/getMonthlyReport")
    suspend fun getMonthlyReport(
        @Query("month") month: String,
        @Query("year") year: String,
        @Header("Authorization") token: String
    ): Response<MonthlyReportResponse>
}
