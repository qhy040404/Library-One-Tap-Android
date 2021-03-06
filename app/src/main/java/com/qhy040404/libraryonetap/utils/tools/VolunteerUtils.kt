package com.qhy040404.libraryonetap.utils.tools

import org.json.JSONObject

object VolunteerUtils {
    fun createVolunteerPostData(name: String, id: String): String {
        val data = JSONObject()
        data.put("name", name)
        data.put("stu_id", id)
        return data.toString()
    }
}