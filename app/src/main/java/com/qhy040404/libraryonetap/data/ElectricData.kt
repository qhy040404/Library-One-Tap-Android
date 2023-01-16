package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.decode

@Suppress("SpellCheckingInspection")
object ElectricData {
    fun getSSMC(data: String) = runCatching {
        data.decode<ElectricDTO>()?.dormitoryInfo_list!![0].SSMC!!
    }.getOrDefault(Constants.GLOBAL_ERROR)

    fun getResele(data: String) = runCatching {
        data.decode<ElectricDTO>()?.dormitoryInfo_list!![0].resele!!
    }.getOrElse {
        if (getFlag(data) == "exception") {
            Constants.API_ERROR
        } else {
            Constants.GLOBAL_ERROR
        }
    }

    private fun getFlag(data: String) = runCatching {
        data.decode<ElectricDTO>()?.dormitoryInfo_list!![0].flag
    }.getOrDefault(Constants.GLOBAL_ERROR)
}

@Suppress("SpellCheckingInspection")
data class ElectricDTO(
    val dormitoryInfo_list: List<ElectricInnerDTO>? = null,
    val flag: String,
)

@Suppress("SpellCheckingInspection")
data class ElectricInnerDTO(
    val SSMC: String? = null,
    val ZSBH: String? = null,
    val flag: String,
    val resele: String? = null,
)
