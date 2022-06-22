package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

object CancelData {
    fun getMessage(returnData: String): String {
        return moshi.adapter(CancelDataClass::class.java).fromJson(returnData)?.message!!
    }
}