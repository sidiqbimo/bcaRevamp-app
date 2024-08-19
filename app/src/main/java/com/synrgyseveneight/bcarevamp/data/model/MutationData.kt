package com.synrgyseveneight.bcarevamp.data.model

data class MutationData(
    val mutation_responses: List<MutationResponseData>,
    val page: Int,
    val size: Int,
    val total_pages: Int
)
