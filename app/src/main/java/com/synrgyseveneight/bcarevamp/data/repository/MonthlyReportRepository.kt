package com.synrgyseveneight.bcarevamp.data.repository

import com.synrgyseveneight.bcarevamp.data.network.ApiService

class MonthlyReportRepository(private val apiService: ApiService) {
    suspend fun getMonthlyReport(month: String, year: String, token: String) =
        apiService.getMonthlyReport(month, year, "Bearer $token")
}