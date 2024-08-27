package com.synrgyseveneight.bcarevamp.data.model.qris

import com.synrgyseveneight.bcarevamp.data.model.auth.User

data class QRISResponse (
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: QrisTransferData?
)

data class QrisTransferData (
    val source_user: User,
    val destination_user: User,
    val amount: Int,
    val admin_fee: Int,
    val total_amount: Int,
    val note: String
)