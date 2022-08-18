package com.qhy040404.libraryonetap.annotation

import androidx.annotation.StringDef

@StringDef(OrderModes.YANXIUJIAN, OrderModes.DETAIL)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OrderModes {
    companion object {
        const val YANXIUJIAN = "1"
        const val DETAIL = "2"
    }
}