package com.synrgyseveneight.bcarevamp.data.repository

import com.synrgyseveneight.bcarevamp.data.model.MutationRequest
import com.synrgyseveneight.bcarevamp.data.network.ApiService
class MutationRepository(private val apiService: ApiService) {
    suspend fun getAllMutations(page: Int, size: Int, token: String, requestBody: MutationRequest) =
        apiService.getAllMutation(page, size, "Bearer $token", requestBody)
}

