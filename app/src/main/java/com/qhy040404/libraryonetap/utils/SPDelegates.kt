package com.qhy040404.libraryonetap.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SPDelegates<T>(private val key: String, private val default: T) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return SPUtils.getValue(key, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        //Nothing
    }
}