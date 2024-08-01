package com.synrgyseveneight.bcarevamp.data.model

data class SignInRequest(
    val signature: String,
    val password: String
)