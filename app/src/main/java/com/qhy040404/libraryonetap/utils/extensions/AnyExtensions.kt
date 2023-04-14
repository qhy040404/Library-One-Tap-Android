package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.utils.JsonUtils

object AnyExtensions {
    /**
     * Convert any data class to json string
     *
     * @return JSON String
     */
    fun Any?.toJson(): String? = JsonUtils.toJson(this)
}
