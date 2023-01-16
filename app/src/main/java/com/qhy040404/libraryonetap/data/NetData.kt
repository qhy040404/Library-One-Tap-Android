package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.decode

object NetData {
    fun getFee(data: String) = runCatching {
        data.decode<NetDTO>()?.fee!!
    }.getOrDefault(Constants.GLOBAL_ERROR)

    fun getDynamicUsedFlow(data: String) = runCatching {
        data.decode<NetDTO>()?.dynamicUsedFlow!!
    }.getOrDefault(Constants.GLOBAL_ERROR)

    fun getDynamicRemainFlow(data: String) = runCatching {
        data.decode<NetDTO>()?.dynamicRemainFlow!!
    }.getOrDefault(Constants.GLOBAL_ERROR)
}

data class NetDTO(
    val fee: String? = null,
    val dynamicRemainFlow: String? = null,
    val dynamicUsedFlow: String? = null,
    val flag: String,
)
