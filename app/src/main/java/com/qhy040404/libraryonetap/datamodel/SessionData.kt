package com.qhy040404.libraryonetap.datamodel

import com.google.gson.Gson
import java.io.Serializable

class SessionData {
    fun isSuccess(returnData: String?): Boolean {
        return Gson().fromJson(returnData, GsonData::class.java).success
    }

    private class GsonData : Serializable {
        val success = false
    }
}