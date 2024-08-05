package com.synrgyseveneight.bcarevamp.data.model

data class SearchAccountResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: AccountData
)

data class AccountData(
    val no: String,
    val name: String,
    val bank: String,
    val image_path: String
)