package com.synrgyseveneight.bcarevamp.data.repository

import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.model.auth.AuthRequest
import com.synrgyseveneight.bcarevamp.data.model.auth.AuthResponse
import com.synrgyseveneight.bcarevamp.data.model.balance.BalanceResponse
import com.synrgyseveneight.bcarevamp.data.model.searchaccount.SearchAccountResponse
import com.synrgyseveneight.bcarevamp.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.Call


class AuthRepository (private val apiService: ApiService, private val dataStore: AuthDataStore) {
    // Fungsi untuk melakukan sign-in
    fun signIn(request: AuthRequest): Call<AuthResponse> {
        return apiService.signIn(request)
    }

    // Fungsi untuk menyimpan data pengguna
    suspend fun saveUserData(signature: String, name: String, accountNumber: String, imagePath: String, bankName: String, token: String) {
        dataStore.saveUserData(signature, name, accountNumber, imagePath, bankName, token)
    }

    // Mendapatkan saldo
    suspend fun getBalance(token: String): BalanceResponse? {
    val response = apiService.getBalance("Bearer $token")
    return if (response.isSuccessful) response.body() else null
    }

    // Search Akun Rekening Tujuan
    suspend fun searchAccount(token: String,accountNumber: String): SearchAccountResponse? {
        val response = apiService.searchAccount("Bearer $token", accountNumber)
        return if (response.isSuccessful) response.body() else null
    }

    // Fungsi untuk menghapus token
    suspend fun clearToken() {
        dataStore.clearToken()
    }

    // Mendapatkan tanda tangan pengguna
    fun getUserSignature(): Flow<String?> {
        return dataStore.userSignature
    }

    // Mendapatkan nama pengguna
    fun getUserName(): Flow<String?> {
        return dataStore.userName
    }

    // Mendapatkan nomor rekening pengguna
    fun getUserAccountNumber(): Flow<String?> {
        return dataStore.userAccountNumber
    }

    // Mendapatkan jalur gambar avatar pengguna
    fun getUserImagePath(): Flow<String?> {
        return dataStore.userImagePath
    }

    // Mendapatkan nama bank pengguna
    fun getUserBankName(): Flow<String?> {
        return dataStore.userBankName
    }

    // Mendapatkan token otentikasi
    fun getUserToken(): Flow<String?> {
        return dataStore.userToken
    }

    // Dapatkan saldo
    fun getUserBalance(): Flow<String?> {
        return dataStore.userBalance
    }

}