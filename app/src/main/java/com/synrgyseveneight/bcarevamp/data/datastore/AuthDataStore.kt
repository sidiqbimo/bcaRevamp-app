package com.synrgyseveneight.bcarevamp.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthDataStore private constructor(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

    companion object {
        @Volatile
        private var INSTANCE: AuthDataStore? = null

        fun getInstance(context: Context): AuthDataStore {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthDataStore(context)
                INSTANCE = instance
                instance
            }
        }

        val USER_SIGNATURE_KEY = stringPreferencesKey("user_signature")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_ACCOUNT_NUMBER_KEY = stringPreferencesKey("user_account_number")
        val USER_AVATAR_PATH_KEY = stringPreferencesKey("user_avatar_path")
        val USER_BANK_NAME_KEY = stringPreferencesKey("user_bank_name")
        val USER_TOKEN_KEY = stringPreferencesKey("user_token")

        val USER_BALANCE_KEY = stringPreferencesKey("user_balance")
        val USER_BALANCE_RETRIEVETIME_KEY = stringPreferencesKey("user_balance_retrievetime")
    }

    // Mendapatkan tanda tangan pengguna
    val userSignature: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_SIGNATURE_KEY]
        }

    // Mendapatkan nama pengguna
    val userName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY]
        }

    // Mendapatkan nomor rekening pengguna
    val userAccountNumber: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ACCOUNT_NUMBER_KEY]
        }

    // Mendapatkan jalur gambar avatar pengguna
    val userAvatarPath: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_AVATAR_PATH_KEY]
        }

    // Mendapatkan nama bank pengguna
    val userBankName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_BANK_NAME_KEY]
        }

    // Mendapatkan token otentikasi
    val userToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN_KEY]
        }

    // Mendapatkan saldo
    val userBalance: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_BALANCE_KEY]
        }

    // Menyimpan data pengguna
    suspend fun saveUserData(signature: String, name: String, accountNumber: String, avatarPath: String, bankName: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_SIGNATURE_KEY] = signature
            preferences[USER_NAME_KEY] = name
            preferences[USER_ACCOUNT_NUMBER_KEY] = accountNumber
            preferences[USER_AVATAR_PATH_KEY] = avatarPath
            preferences[USER_BANK_NAME_KEY] = bankName
            preferences[USER_TOKEN_KEY] = token
        }
    }

    // Simpan keterangan saldo
    suspend fun saveBalance(balance: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_BALANCE_KEY] = balance
        }
    }

    // Simpan waktu get saldo
    suspend fun saveBalanceRetrieveTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_BALANCE_RETRIEVETIME_KEY] = time
        }
    }

    // Menghapus token
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN_KEY)
        }
    }
}
