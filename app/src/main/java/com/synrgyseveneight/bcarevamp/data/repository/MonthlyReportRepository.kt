package com.synrgyseveneight.bcarevamp.data.repository

import com.synrgyseveneight.bcarevamp.data.model.MonthlyReportResponse
import com.synrgyseveneight.bcarevamp.data.network.ApiService
import okhttp3.ResponseBody
import retrofit2.Response

class MonthlyReportRepository(private val apiService: ApiService) {
    suspend fun getMonthlyReport(month: String, year: String, token: String)
    = apiService.getMonthlyReport(month, year, "Bearer $token")

}