package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.utils.extensions.StringExtension.decode

object CancelData {
    fun getMessage(ret: String) = ret.decode<CancelDTO>()?.message!!
}

data class CancelDTO(
    val success: Boolean,
    val message: String? = null,
)
