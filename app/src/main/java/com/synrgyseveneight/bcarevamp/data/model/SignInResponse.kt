package com.synrgyseveneight.bcarevamp.data.model

import android.accounts.Account

data class SignInResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: SignInData
)

data class SignInData(
    val user: Account,
    val token: String
)