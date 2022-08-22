package com.qhy040404.libraryonetap.utils

import java.util.Calendar

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

    fun isValidReserveTime(): Boolean {
        val now = now()
        if (now[0] in 7..22) return true
        if (now[0] == 6 && now[1] > 30) return true
        return false
    }

    private fun timeSingleToDouble(sTime: Int) = if (sTime >= 10) {
        sTime.toString()
    } else {
        "0$sTime"
    }

    private fun now(): Array<Int> {
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        return arrayOf(hour, minute)
    }
}
