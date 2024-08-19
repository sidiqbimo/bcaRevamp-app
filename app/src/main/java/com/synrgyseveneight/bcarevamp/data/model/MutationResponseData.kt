package com.synrgyseveneight.bcarevamp.data.model

data class MutationResponseData(
    val transaction_id: String,
    val unique_code: String,
    val type: String,
    val total_amount: Int,
    val time: String,
    val reference_number: String,
    val destination_account_number: String?,
    val destination_phone_number: String?,
    val formatted_date: String,
    val formatted_time: String
)