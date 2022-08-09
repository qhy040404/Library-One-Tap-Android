package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.NetDataClass

object NetData {
    fun getFee(data: String): String {
        return try {
            moshi.adapter(NetDataClass::class.java).fromJson(data)?.fee!!
        } catch (_: Exception) {
            Constants.GLOBAL_ERROR
        }
    }

    fun getDynamicUsedFlow(data: String): String {
        return try {
            moshi.adapter(NetDataClass::class.java).fromJson(data)?.dynamicUsedFlow!!
        } catch (_: Exception) {
            Constants.GLOBAL_ERROR
        }
    }

    fun getDynamicRemainFlow(data: String): String {
        return try {
            moshi.adapter(NetDataClass::class.java).fromJson(data)?.dynamicRemainFlow!!
        } catch (_: Exception) {
            Constants.GLOBAL_ERROR
        }
    }
}