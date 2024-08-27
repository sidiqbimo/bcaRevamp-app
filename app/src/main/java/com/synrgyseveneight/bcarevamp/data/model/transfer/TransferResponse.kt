package com.synrgyseveneight.bcarevamp.data.model.transfer

import com.synrgyseveneight.bcarevamp.data.model.auth.User

data class TransferResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: TransferData?
)

data class TransferData(
    val source_user: User,
    val destination_user: User,
    val amount: Int,
    val admin_fee: Int,
    val total_amount: Int,
    val note: String
)

data class SenderInfo(
    val name: String,
    val bank: String,
    val account_number: String,
    val image_path: String
)
