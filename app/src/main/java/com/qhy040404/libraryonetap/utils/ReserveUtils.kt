package com.qhy040404.libraryonetap.utils

object ReserveUtils {
    fun getResetRoomCode(space_name: String): Int {
        val area = space_name.split("图书馆")[0]
        val room = space_name.split("图书馆")[1].split("阅")[0]
        return RoomUtils.getRoomCode(area, room)
    }

    fun constructPara(room: Int): String {
        return "room_id=$room&order_date=${getToday("/",false)}"
    }

    fun constructParaForAddCode(seat_id: String): String {
        return "seat_id=$seat_id&order_date=${getToday("/",false)}"
    }

    fun constructParaForFinalReserve(addCode: String): String {
        return "addCode=$addCode&method=addSeat"
    }

    fun formatAvailableMap(am: String): String {
        return am.replace("\r\n\r\n[[", "").replace("]]\r\n\r\n\r\n\r\n", "")
            .replace("{", "").replace("}", "")
    }
}