package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson
import java.io.Serializable

object SessionData {
    fun isSuccess(returnData: String?): Boolean {
        return gson.fromJson(returnData, GsonData::class.java).success
    }

    private class GsonData : Serializable {
        val success = false
    }
}