package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.VolunteerDataClass

object VolunteerData {
    fun getSameID(data: String) = try {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.numSameID!!
    } catch (_: Exception) {
        -1
    }

    fun getSameName(data: String) = try {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.numSameName!!
    } catch (_: Exception) {
        -1
    }

    fun getTotalHours(data: String) = try {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.totalDuration!!
    } catch (_: Exception) {
        -1.0
    }
}