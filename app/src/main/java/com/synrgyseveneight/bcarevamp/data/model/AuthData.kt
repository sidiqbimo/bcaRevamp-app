package com.synrgyseveneight.bcarevamp.data.model
import com.synrgyseveneight.bcarevamp.data.model.User

data class AuthData(
    val user: User,
    val token: String
)