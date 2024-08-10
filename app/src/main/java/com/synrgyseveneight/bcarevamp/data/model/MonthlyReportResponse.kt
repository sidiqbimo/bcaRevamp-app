package com.synrgyseveneight.bcarevamp.data.model

data class MonthlyReportResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: MonthlyReportData
)