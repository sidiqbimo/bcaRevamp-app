package com.synrgyseveneight.bcarevamp.data.model

data class TransferRequest(
    val destinationAccountNumber: String,
    val amount: Int,
    val mpin: String,
    val note: String,
    val savedAccount: Boolean
)
