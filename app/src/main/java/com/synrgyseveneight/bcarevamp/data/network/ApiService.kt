package com.synrgyseveneight.bcarevamp.data.network

import com.synrgyseveneight.bcarevamp.data.model.AuthRequest
import com.synrgyseveneight.bcarevamp.data.model.AuthResponse
import com.synrgyseveneight.bcarevamp.data.model.BalanceResponse
import com.synrgyseveneight.bcarevamp.data.model.MonthlyReportResponse
import com.synrgyseveneight.bcarevamp.data.model.QRISResponse
import com.synrgyseveneight.bcarevamp.data.model.QRISTransferRequest
import com.synrgyseveneight.bcarevamp.data.model.MutationRequest
import com.synrgyseveneight.bcarevamp.data.model.MutationResponse
import com.synrgyseveneight.bcarevamp.data.model.SearchAccountResponse
import com.synrgyseveneight.bcarevamp.data.model.SearchQrisResponse
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
import okhttp3.ResponseBody;
import retrofit2.http.Streaming

interface ApiService {
    // Endpoint untuk sign-in
    @POST("api/v1/auth/sign-in")
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
    @GET("api/v1/transactions/get-monthly-report")
    suspend fun getMonthlyReport(
        @Query("month") month: String,
        @Query("year") year: String,
        @Header("Authorization") token: String
    ): Response<MonthlyReportResponse>


    // Info QRIS
    @GET("api/v1/merchants/qris/{id_qris}")
    suspend fun searchQris(@Header("Authorization") token: String, @Path("id_qris") id_qris: String): Response<SearchQrisResponse>

    // Transfer QRIS
    @POST("api/v1/transactions/merchant-transaction")
    suspend fun performQrisTransfer(
        @Header("Authorization") token: String,
        @Body request: QRISTransferRequest
    ): Response<QRISResponse>

    // Endpoint untuk mendapatkan data mutasi
    @POST("api/v1/transactions/get-all-mutation")
    suspend fun getAllMutation(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") token: String,
        @Body requestBody: MutationRequest
    ): Response<MutationResponse>

    @Streaming
    @GET("api/v1/transactions/generate-all-mutation-report")
    suspend fun downloadMutationReport(
        @Header("Authorization") token: String
    ): Response<ResponseBody>
}
