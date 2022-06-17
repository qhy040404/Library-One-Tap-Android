package com.qhy040404.libraryonetap.utils

import java.util.*

object TimeUtils {
    private val calendar: Calendar = Calendar.getInstance()

    fun getToday(separator: String, doubleNum: Boolean): String {
        val year = calendar[Calendar.YEAR].toString()
        val month = if (doubleNum) {
            timeSingleToDouble(calendar[Calendar.MONTH] + 1)
        } else {
            (calendar[Calendar.MONTH] + 1).toString()
        }
        val day = if (doubleNum) {
            timeSingleToDouble(calendar[Calendar.DAY_OF_MONTH])
        } else {
            calendar[Calendar.DAY_OF_MONTH].toString()
        }
        return year + separator + month + separator + day
    }

    private fun timeSingleToDouble(sTime: Int): String {
        return if (sTime >= 10) {
            sTime.toString()
        } else {
            "0$sTime"
        }
    }
}