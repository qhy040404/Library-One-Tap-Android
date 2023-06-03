package com.qhy040404.libraryonetap.constant

import com.qhy040404.libraryonetap.utils.lazy.resettableManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object GlobalManager {
    val lazyMgr = resettableManager()
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
}
