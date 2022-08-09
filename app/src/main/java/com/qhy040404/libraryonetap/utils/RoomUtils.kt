package com.qhy040404.libraryonetap.utils

object RoomUtils {
    fun getRoomCode(area: String, room: String) = when (area) {
        "伯川", "Bochuan" -> when (room) {
            "301" -> 168
            "312" -> 170
            "401" -> 195
            "404" -> 197
            "409" -> 196
            "501" -> 198
            "504" -> 199
            "507" -> 200
            else -> {
                val temp = intArrayOf(168, 170, 195, 197, 196, 198, 199, 200)
                temp[RandomDataUtils.getNum(8)]
            }
        }
        "令希", "Lingxi" -> when (room) {
            "301" -> 207
            "302" -> 208
            "401" -> 205
            "402" -> 206
            "501" -> 203
            "502" -> 204
            "601" -> 201
            "602" -> 202
            else -> {
                val temp = intArrayOf(201, 202, 203, 204, 205, 206, 207, 208)
                temp[RandomDataUtils.getNum(8)]
            }
        }
        else -> 0
    }
}