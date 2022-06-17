package com.qhy040404.libraryonetap.datamodel

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Suppress("SpellCheckingInspection")
object ElectricData {
    fun getSSMC(data: String?): String {
        return Gson().fromJson(data, GsonData::class.java).dormitoryInfo_list!![0].ssmc!!
    }

    fun getResele(data: String?): String {
        return Gson().fromJson(data, GsonData::class.java).dormitoryInfo_list!![0].resele!!
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