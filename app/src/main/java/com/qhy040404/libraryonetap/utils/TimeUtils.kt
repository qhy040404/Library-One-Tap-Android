package com.qhy040404.libraryonetap.utils

fun timeSingleToDouble(sTime: Int): String {
    val resultTime: String = if (sTime >= 10) {
        sTime.toString()
    } else {
        "0$sTime"
    }
    return resultTime
}