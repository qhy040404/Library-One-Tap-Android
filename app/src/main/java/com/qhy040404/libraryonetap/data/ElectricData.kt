package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.ElectricDataClass

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