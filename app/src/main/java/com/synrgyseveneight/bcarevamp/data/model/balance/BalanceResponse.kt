package com.synrgyseveneight.bcarevamp.data.model.balance

data class BalanceResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: BalanceData
)
