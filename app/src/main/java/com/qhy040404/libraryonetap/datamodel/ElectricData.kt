package com.qhy040404.libraryonetap.datamodel

import com.google.gson.annotations.SerializedName
import com.qhy040404.libraryonetap.constant.GlobalManager.gson
import java.io.Serializable

@Suppress("SpellCheckingInspection")
object ElectricData {
    fun getSSMC(data: String?): String {
        return gson.fromJson(data, GsonData::class.java).dormitoryInfo_list!![0].ssmc!!
    }

    fun getResele(data: String?): String {
        return gson.fromJson(data, GsonData::class.java).dormitoryInfo_list!![0].resele!!
    }

    @Suppress("PropertyName")
    private class GsonData : Serializable {
        val dormitoryInfo_list: List<DormitoryInfoListBean>? = null

        class DormitoryInfoListBean : Serializable {
            @SerializedName("SSMC")
            val ssmc: String? = null
            val resele: String? = null
        }
    }
}