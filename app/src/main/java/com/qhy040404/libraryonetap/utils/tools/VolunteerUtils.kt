package com.qhy040404.libraryonetap.utils.tools

import com.qhy040404.libraryonetap.utils.extensions.AnyExtensions.toJson

object VolunteerUtils {
    fun createVolunteerPostData(name: String, id: String) =
        VolunteerDataClass(name, id).toJson().toString()

    data class VolunteerDataClass(
        val name: String? = null,
        val stu_id: String? = null,
    )
}
