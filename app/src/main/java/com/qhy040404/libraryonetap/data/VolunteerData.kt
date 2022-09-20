package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.VolunteerDataClass

object VolunteerData {
    fun getSameID(data: String) = runCatching {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data.trim())?.numSameID!!
    }.getOrDefault(-1)

    fun getSameName(data: String) = runCatching {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data.trim())?.numSameName!!
    }.getOrDefault(-1)

    fun getTotalHours(data: String) = runCatching {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data.trim())?.totalDuration!!
    }.getOrDefault(-1.0)
}
