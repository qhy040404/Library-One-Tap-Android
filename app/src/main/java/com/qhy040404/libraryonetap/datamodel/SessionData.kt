package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson

object SessionData {
    fun isSuccess(returnData: String?): Boolean {
        return gson.fromJson(returnData, SessionDataClass::class.java).success
    }
}