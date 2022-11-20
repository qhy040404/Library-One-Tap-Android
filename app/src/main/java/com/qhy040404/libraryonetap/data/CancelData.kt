package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

object CancelData {
    fun getMessage(returnData: String) =
        moshi.adapter(CancelDTO::class.java).fromJson(returnData.trim())?.message!!
}

data class CancelDTO(
    val success: Boolean,
    val message: String? = null,
)
