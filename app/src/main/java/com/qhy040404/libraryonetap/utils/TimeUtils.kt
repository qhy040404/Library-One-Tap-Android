package com.qhy040404.libraryonetap.utils

import com.qhy040404.datetime.Datetime

object TimeUtils {
    fun getToday(separator: String, doubleNum: Boolean): String {
        val now = Datetime.now()
        val year = now.year.toString()
        val month = if (doubleNum) {
            timeSingleToDouble(now.month)
        } else {
            now.month.toString()
        }
        val day = if (doubleNum) {
            timeSingleToDouble(now.day)
        } else {
            now.day.toString()
        }
        return "$year$separator$month$separator$day"
    }

    fun isValidReserveTime(): Boolean {
        return Datetime.now().let {
            it.hour in 7..22 || it.hour == 6 && it.minute > 30
        }
    }

    fun isServerAvailableTime(): Boolean {
        return Datetime.now().hour in 6..23
    }

    private fun timeSingleToDouble(sTime: Int) = if (sTime >= 10) {
        sTime.toString()
    } else {
        "0$sTime"
    }
}
