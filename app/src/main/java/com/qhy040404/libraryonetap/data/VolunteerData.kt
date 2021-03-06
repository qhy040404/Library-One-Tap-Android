package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.VolunteerDataClass

object VolunteerData {
    fun getSameID(data: String): Int {
        return moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.numSameID!!
    }

    fun getSameName(data: String): Int {
        return moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.numSameName!!
    }

    fun getTotalHours(data: String): Double {
        return moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.totalDuration!!
    }
}