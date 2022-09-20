package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.ElectricDataClass

@Suppress("SpellCheckingInspection")
object ElectricData {
    fun getSSMC(data: String) = runCatching {
        moshi.adapter(ElectricDataClass::class.java)
            .fromJson(data.trim())?.dormitoryInfo_list!![0].SSMC!!
    }.getOrDefault(Constants.GLOBAL_ERROR)

    fun getResele(data: String) = runCatching {
        moshi.adapter(ElectricDataClass::class.java)
            .fromJson(data.trim())?.dormitoryInfo_list!![0].resele!!
    }.getOrDefault(Constants.GLOBAL_ERROR)
}
