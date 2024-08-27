package com.synrgyseveneight.bcarevamp.data.model.qris

data class SearchQrisResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: QrisData
)

data class QrisData(
    val name : String,
    val nmid : String,
    val terminal_id : String,
    val amount : Int,
    val image_path: String,
    val address : String,
    val qris_code : String
)