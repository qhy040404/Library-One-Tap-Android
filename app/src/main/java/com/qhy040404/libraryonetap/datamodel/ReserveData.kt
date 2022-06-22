package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

object ReserveData {
    fun getAddCode(data: String): String {
        return moshi.adapter(ReserveDataClass::class.java).fromJson(data)?.data!!.addCode!!
    }
}