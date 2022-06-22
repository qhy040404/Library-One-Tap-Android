package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

object NetData {
    fun getFee(data: String): String {
        return moshi.adapter(NetDataClass::class.java).fromJson(data)?.fee!!
    }

    fun getDynamicUsedFlow(data: String): String {
        return moshi.adapter(NetDataClass::class.java).fromJson(data)?.dynamicUsedFlow!!
    }

    fun getDynamicRemainFlow(data: String): String {
        return moshi.adapter(NetDataClass::class.java).fromJson(data)?.dynamicRemainFlow!!
    }
}