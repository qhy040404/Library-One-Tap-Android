package com.qhy040404.libraryonetap.utils

import java.util.*

val calendar: Calendar = Calendar.getInstance()

fun getToday(): String {
    return calendar[Calendar.YEAR].toString() + "/" + (calendar[Calendar.MONTH] + 1).toString() + "/" + calendar[Calendar.DAY_OF_MONTH].toString()
}

fun timeSingleToDouble(sTime: Int): String {
    val resultTime: String = if (sTime >= 10) {
        sTime.toString()
    } else {
        "0$sTime"
    }
    return resultTime
}