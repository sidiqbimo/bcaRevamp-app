package com.synrgyseveneight.bcarevamp.data.network

import com.synrgyseveneight.bcarevamp.data.model.auth.AuthRequest
import com.synrgyseveneight.bcarevamp.data.model.auth.AuthResponse
import com.synrgyseveneight.bcarevamp.data.model.balance.BalanceResponse
import com.synrgyseveneight.bcarevamp.data.model.mothlyreport.MonthlyReportResponse
import com.synrgyseveneight.bcarevamp.data.model.qris.QRISResponse
import com.synrgyseveneight.bcarevamp.data.model.qris.QRISTransferRequest
import com.synrgyseveneight.bcarevamp.data.model.mutation.MutationRequest
import com.synrgyseveneight.bcarevamp.data.model.mutation.MutationResponse
import com.synrgyseveneight.bcarevamp.data.model.searchaccount.SearchAccountResponse
import com.synrgyseveneight.bcarevamp.data.model.qris.SearchQrisResponse
import com.synrgyseveneight.bcarevamp.data.model.transfer.TransferRequest
import com.synrgyseveneight.bcarevamp.data.model.transfer.TransferResponse
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
