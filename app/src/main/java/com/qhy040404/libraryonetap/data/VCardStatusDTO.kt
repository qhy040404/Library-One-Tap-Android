package com.qhy040404.libraryonetap.data

data class VCardStatusDTO(
    val message: String,
    val resultData: ResultData,
    val success: Boolean,
)

@Suppress("SpellCheckingInspection")
data class ResultData(
    val message: String,
    val paytime: String,
    val status: String,
)
