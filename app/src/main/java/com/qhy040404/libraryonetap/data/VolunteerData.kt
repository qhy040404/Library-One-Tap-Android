package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi

object VolunteerData {
    fun getSameID(data: String) = runCatching {
        moshi.adapter(VolunteerDTO::class.java).fromJson(data.trim())?.numSameID!!
    }.getOrDefault(-1)

    fun getSameName(data: String) = runCatching {
        moshi.adapter(VolunteerDTO::class.java).fromJson(data.trim())?.numSameName!!
    }.getOrDefault(-1)

    fun getTotalHours(data: String) = runCatching {
        moshi.adapter(VolunteerDTO::class.java).fromJson(data.trim())?.totalDuration!!
    }.getOrDefault(-1.0)
}

data class VolunteerDTO(
    val numSameID: Int = 0,
    val numSameName: Int = 0,
    val totalDuration: Double = 0.0,
)
