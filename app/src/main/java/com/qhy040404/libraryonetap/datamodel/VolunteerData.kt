package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson
import java.io.Serializable

object VolunteerData {
    fun getSameID(data: String?): Int {
        return gson.fromJson(data, GsonData::class.java).numSameID
    }

    fun getSameName(data: String?): Int {
        return gson.fromJson(data, GsonData::class.java).numSameName
    }

    fun getTotalHours(data: String?): Double {
        return gson.fromJson(data, GsonData::class.java).totalDuration
    }

    private class GsonData : Serializable {
        val numSameID = 0
        val numSameName = 0
        val totalDuration = 0.0
    }
}