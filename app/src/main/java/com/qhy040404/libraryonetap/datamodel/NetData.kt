package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson

object NetData {
    fun getFee(data: String?): String {
        return gson.fromJson(data, NetDataClass::class.java).fee!!
    }

    fun getDynamicUsedFlow(data: String?): String {
        return gson.fromJson(data, NetDataClass::class.java).dynamicUsedFlow!!
    }

    fun getDynamicRemainFlow(data: String?): String {
        return gson.fromJson(data, NetDataClass::class.java).dynamicRemainFlow!!
    }
}