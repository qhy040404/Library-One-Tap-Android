package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.SessionDataClass

object SessionData {
    fun isSuccess(returnData: String) = try {
        moshi.adapter(SessionDataClass::class.java).fromJson(returnData)?.success!!
    } catch (_: Exception) {
        false
    }
}
