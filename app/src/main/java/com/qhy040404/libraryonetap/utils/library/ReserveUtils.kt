package com.qhy040404.libraryonetap.utils.library

import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.extensions.substringBetween

object ReserveUtils {
  fun getResetRoomCode(space: String): Int {
    val area = space.substringBefore("图书馆")
    val room = space.substringBetween("图书馆", "阅")
    return RoomUtils.getRoomCode(area, room)
  }

  fun constructPara(room: Int) = "room_id=$room&order_date=${TimeUtils.getToday("/", false)}"

  fun constructParaForAddCode(seatId: String) =
    "seat_id=$seatId&order_date=${TimeUtils.getToday("/", false)}"

  fun constructParaForFinalReserve(addCode: String) = "addCode=$addCode&method=addSeat"

  fun formatAvailableMap(am: String) = am.trim()
    .replace("[[", "")
    .replace("]]", "")
    .replace("{", "")
    .replace("}", "")
}
