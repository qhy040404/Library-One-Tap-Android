package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.ReserveDataClass

object ReserveData {
    fun getAddCode(data: String) =
        moshi.adapter(ReserveDataClass::class.java).fromJson(data)?.data!!.addCode!!
}