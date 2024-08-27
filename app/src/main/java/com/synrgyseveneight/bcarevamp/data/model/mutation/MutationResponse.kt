package com.synrgyseveneight.bcarevamp.data.model.mutation

data class MutationResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: MutationData
)