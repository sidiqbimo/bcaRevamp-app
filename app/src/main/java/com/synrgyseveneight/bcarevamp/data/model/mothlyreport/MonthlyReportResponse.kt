package com.synrgyseveneight.bcarevamp.data.model.mothlyreport

data class MonthlyReportResponse(
    val code: Int,
    val message: String,
    val status: Boolean,
    val data: MonthlyReportData
)