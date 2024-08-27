package com.synrgyseveneight.bcarevamp.data.model.mutation

data class MutationRequest(
    val startDate: String,
    val endDate: String,
    val transactionCategory: String
)