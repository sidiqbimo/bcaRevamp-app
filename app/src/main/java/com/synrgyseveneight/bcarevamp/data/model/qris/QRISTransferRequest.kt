package com.synrgyseveneight.bcarevamp.data.model.qris

data class QRISTransferRequest (
    val idQris: String,
    val amount: Int,
    val note: String,
    val mpin: String
)