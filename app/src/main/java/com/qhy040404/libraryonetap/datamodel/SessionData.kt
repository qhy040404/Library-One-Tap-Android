package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

object SessionData {
    fun isSuccess(returnData: String): Boolean {
        return moshi.adapter(SessionDataClass::class.java).fromJson(returnData)?.success!!
    }
}