package com.qhy040404.libraryonetap.datamodel

import com.google.gson.Gson
import java.io.Serializable

object ReserveData {
    fun getAddCode(data: String?): String {
        return Gson().fromJson(data, GsonData::class.java).data!!.addCode!!
    }

    private class GsonData : Serializable {
        val data: DataBean? = null

        class DataBean : Serializable {
            val addCode: String? = null
        }
    }
}