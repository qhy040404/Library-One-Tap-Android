package com.qhy040404.libraryonetap.data.model

data class VCardStatusClass(
    val message: String,
    val resultData: ResultData,
    val success: Boolean,
)

data class ResultData(
    val message: String,
    val paytime: String,
    val status: String,
)
