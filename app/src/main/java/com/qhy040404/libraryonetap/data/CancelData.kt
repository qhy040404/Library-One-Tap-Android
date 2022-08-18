package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.CancelDataClass

object CancelData {
    fun getMessage(returnData: String) =
        moshi.adapter(CancelDataClass::class.java).fromJson(returnData)?.message!!
}
