package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.JsonUtils

object AnyExtensions {
    /**
     * Convert any data class to json string
     *
     * @return JSON String
     */
    fun Any?.toJson(): String? = JsonUtils.toJson(this)

    /**
     * Throw the data of the value.
     *
     * To ensure a val can get the expected data.
     * @return Unit
     */
    fun Any?.throwData() = AppUtils.pass(this)
}
