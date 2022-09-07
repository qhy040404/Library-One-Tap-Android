package com.qhy040404.libraryonetap.utils

import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.constant.GlobalValues
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PasswordDelegates(private val data: String) : ReadWriteProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String =
        GlobalManager.des.strDec(data, "q", "h", "y")

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        GlobalValues.passwdEnc = value
    }
}
