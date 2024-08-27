package com.synrgyseveneight.bcarevamp.data.model.auth
data class AuthRequest(
    val signature: String,
    val password: String
)