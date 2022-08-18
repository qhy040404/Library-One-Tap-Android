package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.ElectricDataClass

@Suppress("SpellCheckingInspection")
object ElectricData {
    fun getSSMC(data: String) = try {
        moshi.adapter(ElectricDataClass::class.java)
            .fromJson(data)?.dormitoryInfo_list!![0].SSMC!!
    } catch (_: Exception) {
        Constants.GLOBAL_ERROR
    }

    fun getResele(data: String) = try {
        moshi.adapter(ElectricDataClass::class.java)
            .fromJson(data)?.dormitoryInfo_list!![0].resele!!
    } catch (_: Exception) {
        Constants.GLOBAL_ERROR
    }
}
