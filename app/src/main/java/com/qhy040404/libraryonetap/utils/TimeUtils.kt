package com.qhy040404.libraryonetap.utils

import com.qhy040404.datetime.Datetime
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
        val now = Datetime.now()
        if (now.hour in 7..22) {
            return true
        }
        if (now.hour == 6 && now.minute > 30) {
            return true
        }
        return false
    }

    private fun timeSingleToDouble(sTime: Int) = if (sTime >= 10) {
        sTime.toString()
    } else {
        "0$sTime"
    }
}
