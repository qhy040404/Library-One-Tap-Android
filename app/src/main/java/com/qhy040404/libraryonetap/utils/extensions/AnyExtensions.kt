package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.utils.JsonUtils

object AnyExtensions {
    fun Any?.toJson(): String? = JsonUtils.toJson(this)
}