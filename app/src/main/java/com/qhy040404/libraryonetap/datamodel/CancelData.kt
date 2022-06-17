package com.qhy040404.libraryonetap.datamodel

import com.google.gson.Gson
import java.io.Serializable

object CancelData {
    fun getMessage(returnData: String?): String {
        return Gson().fromJson(returnData, GsonData::class.java).message!!
    }

    private class GsonData : Serializable {
        val message: String? = null
    }
}