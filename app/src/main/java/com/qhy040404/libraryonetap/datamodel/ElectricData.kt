package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

@Suppress("SpellCheckingInspection")
object ElectricData {
    fun getSSMC(data: String): String {
        return moshi.adapter(ElectricDataClass::class.java)
            .fromJson(data)?.dormitoryInfo_list!![0].SSMC!!
    }

    fun getResele(data: String): String {
        return moshi.adapter(ElectricDataClass::class.java)
            .fromJson(data)?.dormitoryInfo_list!![0].resele!!
    }
}