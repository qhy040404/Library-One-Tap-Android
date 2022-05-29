package com.qhy040404.libraryonetap.utils

import java.util.*

object ReserveUtils {
    fun constructPara(room: Int): String {
        val calendar = Calendar.getInstance()
        val today =
            calendar[Calendar.YEAR].toString() + "/" + (calendar[Calendar.MONTH] + 1).toString() + "/" + calendar[Calendar.DAY_OF_MONTH].toString()
        return "room_id=$room&order_date=$today"
    }

    fun constructParaForFinalReserve(addCode: String): String {
        return "addCode=$addCode&method=addSeat"
    }
}