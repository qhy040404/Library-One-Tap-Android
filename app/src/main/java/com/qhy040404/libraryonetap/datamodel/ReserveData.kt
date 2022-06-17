package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson
import java.io.Serializable

object ReserveData {
    fun getAddCode(data: String?): String {
        return gson.fromJson(data, GsonData::class.java).data!!.addCode!!
    }

    private class GsonData : Serializable {
        val data: DataBean? = null

        class DataBean : Serializable {
            val addCode: String? = null
        }
    }
}