package com.synrgyseveneight.bcarevamp.data.model.auth

data class User(
    val name: String,
    val account_number: String,
    val signature: String,
    val image_path: String,
    val bank_name: String
)