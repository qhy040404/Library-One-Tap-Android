package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

object NetData {
    fun getFee(data: String) = runCatching {
        moshi.adapter(NetDTO::class.java).fromJson(data.trim())?.fee!!
    }.getOrDefault(Constants.GLOBAL_ERROR)

    fun getDynamicUsedFlow(data: String) = runCatching {
        moshi.adapter(NetDTO::class.java).fromJson(data.trim())?.dynamicUsedFlow!!
    }.getOrDefault(Constants.GLOBAL_ERROR)

    fun getDynamicRemainFlow(data: String) = runCatching {
        moshi.adapter(NetDTO::class.java).fromJson(data.trim())?.dynamicRemainFlow!!
    }.getOrDefault(Constants.GLOBAL_ERROR)
}

data class NetDTO(
    val fee: String? = null,
    val dynamicRemainFlow: String? = null,
    val dynamicUsedFlow: String? = null,
    val flag: String,
)
