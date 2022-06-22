package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson

object VolunteerData {
    fun getSameID(data: String?): Int {
        return gson.fromJson(data, VolunteerDataClass::class.java).numSameID
    }

    fun getSameName(data: String?): Int {
        return gson.fromJson(data, VolunteerDataClass::class.java).numSameName
    }

    fun getTotalHours(data: String?): Double {
        return gson.fromJson(data, VolunteerDataClass::class.java).totalDuration
    }
}