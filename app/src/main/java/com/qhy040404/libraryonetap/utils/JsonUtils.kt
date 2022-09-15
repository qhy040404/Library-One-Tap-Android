package com.qhy040404.libraryonetap.utils

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import org.intellij.lang.annotations.Language

object JsonUtils {
    @Language("JSON")
    inline fun <reified T> toJson(value: T?): String? = runCatching {
        moshi.adapter(T::class.java).toJson(value)
    }.getOrNull()
}
