package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson

object CancelData {
    fun getMessage(returnData: String?): String {
        return gson.fromJson(returnData, CancelDataClass::class.java).message!!
    }
}