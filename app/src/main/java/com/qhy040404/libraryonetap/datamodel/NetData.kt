package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson
import java.io.Serializable

object NetData {
    fun getFee(data: String?): String {
        return gson.fromJson(data, GsonData::class.java).fee!!
    }

    fun getDynamicUsedFlow(data: String?): String {
        return gson.fromJson(data, GsonData::class.java).dynamicUsedFlow!!
    }

    fun getDynamicRemainFlow(data: String?): String {
        return gson.fromJson(data, GsonData::class.java).dynamicRemainFlow!!
    }

    private class GsonData : Serializable {
        val fee: String? = null
        val dynamicRemainFlow: String? = null
        val dynamicUsedFlow: String? = null
    }
}