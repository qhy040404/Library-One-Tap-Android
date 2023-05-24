package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.utils.extensions.decode

object VolunteerData {
    fun getSameID(data: String) = runCatching {
        data.decode<VolunteerDTO>()?.numSameID!!
    }.getOrDefault(-1)

    fun getSameName(data: String) = runCatching {
        data.decode<VolunteerDTO>()?.numSameName!!
    }.getOrDefault(-1)

    fun getTotalHours(data: String) = runCatching {
        data.decode<VolunteerDTO>()?.totalDuration!!
    }.getOrDefault(-1.0)
}

data class VolunteerDTO(
    val numSameID: Int = 0,
    val numSameName: Int = 0,
    val totalDuration: Double = 0.0,
)
