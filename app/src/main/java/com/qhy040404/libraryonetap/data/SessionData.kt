package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

object SessionData {
    fun isSuccess(returnData: String) = runCatching {
        moshi.adapter(SessionDTO::class.java).fromJson(returnData.trim())?.success!!
    }.getOrDefault(false)
}

data class SessionDTO(
    val message: String,
    val success: Boolean,
    val user_id: String,
)
