package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson

@Suppress("SpellCheckingInspection")
object ElectricData {
    fun getSSMC(data: String?): String {
        return gson.fromJson(data, ElectricDataClass::class.java).dormitoryInfo_list!![0].SSMC!!
    }

    fun getResele(data: String?): String {
        return gson.fromJson(data, ElectricDataClass::class.java).dormitoryInfo_list!![0].resele!!
    }
}