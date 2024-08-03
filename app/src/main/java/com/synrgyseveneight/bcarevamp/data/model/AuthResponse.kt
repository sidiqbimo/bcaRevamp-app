package com.synrgyseveneight.bcarevamp.data.model
data class AuthResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: AuthData
)