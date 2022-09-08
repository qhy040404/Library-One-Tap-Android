package com.qhy040404.libraryonetap.annotation

import androidx.annotation.StringDef

@StringDef(NetworkStates.WIFI, NetworkStates.CELLULAR)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class NetworkStates {
    companion object {
        const val WIFI = "WIFI"
        const val CELLULAR = "Cellular"
    }
}
