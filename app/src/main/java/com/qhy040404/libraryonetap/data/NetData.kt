package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.NetDataClass

object NetData {
    fun getFee(data: String) = runCatching {
        moshi.adapter(NetDataClass::class.java).fromJson(data)?.fee!!
    }.getOrDefault(Constants.GLOBAL_ERROR)

    fun getDynamicUsedFlow(data: String) = runCatching {
        moshi.adapter(NetDataClass::class.java).fromJson(data)?.dynamicUsedFlow!!
    }.getOrDefault(Constants.GLOBAL_ERROR)

    fun getDynamicRemainFlow(data: String) = runCatching {
        moshi.adapter(NetDataClass::class.java).fromJson(data)?.dynamicRemainFlow!!
    }.getOrDefault(Constants.GLOBAL_ERROR)
}
