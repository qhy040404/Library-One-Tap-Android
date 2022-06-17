package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson
import java.io.Serializable

object CancelData {
    fun getMessage(returnData: String?): String {
        return gson.fromJson(returnData, GsonData::class.java).message!!
    }

    private class GsonData : Serializable {
        val message: String? = null
    }
}