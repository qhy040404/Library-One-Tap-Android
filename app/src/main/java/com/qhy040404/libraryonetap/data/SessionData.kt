package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.utils.extensions.decode

object SessionData {
    fun isSuccess(ret: String) = runCatching {
        ret.decode<SessionDTO>()?.success!!
    }.getOrDefault(false)
}

data class SessionDTO(
    val message: String,
    val success: Boolean,
    val user_id: String,
)
