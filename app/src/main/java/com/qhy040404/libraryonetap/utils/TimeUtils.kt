package com.qhy040404.libraryonetap.utils

import com.qhy040404.datetime.Datetime
import com.qhy040404.libraryonetap.utils.extensions.one2two

object TimeUtils {
    fun getToday(separator: String, doubleNum: Boolean): String {
        val now = Datetime.now()
        val year = now.year.toString()
        val month = if (doubleNum) {
            now.month.one2two()
        } else {
            now.month.toString()
        }
        val day = if (doubleNum) {
            now.day.one2two()
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
        return Datetime.now().hour in 6 until 23
    }
}
