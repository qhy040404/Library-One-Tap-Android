package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.VolunteerDataClass

object VolunteerData {
    fun getSameID(data: String) = runCatching {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.numSameID!!
    }.getOrDefault(-1)

    fun getSameName(data: String) = runCatching {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.numSameName!!
    }.getOrDefault(-1)

    fun getTotalHours(data: String) = runCatching {
        moshi.adapter(VolunteerDataClass::class.java).fromJson(data)?.totalDuration!!
    }.getOrDefault(-1.0)
}
