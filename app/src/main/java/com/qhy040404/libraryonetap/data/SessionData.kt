package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.SessionDataClass

object SessionData {
    fun isSuccess(returnData: String): Boolean {
        return try {
            moshi.adapter(SessionDataClass::class.java).fromJson(returnData)?.success!!
        } catch (_: Exception) {
            return false
        }
    }
}