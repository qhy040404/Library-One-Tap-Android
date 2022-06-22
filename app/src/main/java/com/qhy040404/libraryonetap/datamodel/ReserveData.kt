package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson

object ReserveData {
    fun getAddCode(data: String?): String {
        return gson.fromJson(data, ReserveDataClass::class.java).data!!.addCode!!
    }
}