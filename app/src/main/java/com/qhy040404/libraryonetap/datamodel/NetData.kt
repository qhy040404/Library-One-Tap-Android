package com.qhy040404.libraryonetap.datamodel

import com.google.gson.Gson
import java.io.Serializable

class NetData {
    fun getFee(data: String?): String? {
        return Gson().fromJson(data, GsonData::class.java).fee
    }

    fun getDynamicUsedFlow(data: String?): String? {
        return Gson().fromJson(data, GsonData::class.java).dynamicUsedFlow
    }

    fun getDynamicRemainFlow(data: String?): String? {
        return Gson().fromJson(data, GsonData::class.java).dynamicRemainFlow
    }

    private class GsonData : Serializable {
        val fee: String? = null
        val dynamicRemainFlow: String? = null
        val dynamicUsedFlow: String? = null
    }
}