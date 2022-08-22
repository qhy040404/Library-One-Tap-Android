package com.qhy040404.libraryonetap.utils.tools

import java.util.Calendar

object BathUtils {
    fun getBathTime(): Int {
        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return when (hour) {
            9 -> when (minute) {
                in 0..19 -> 1244
                in 20..39 -> 1245
                in 40..59 -> 1246
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            10 -> when (minute) {
                in 0..19 -> 1247
                in 20..39 -> 1248
                in 40..59 -> 1249
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            11 -> when (minute) {
                in 0..19 -> 1250
                in 20..39 -> 1251
                in 40..59 -> 1252
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            12 -> when (minute) {
                in 0..19 -> 1253
                in 20..39 -> 1254
                in 40..59 -> 1255
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            13 -> when (minute) {
                in 0..19 -> 1256
                in 20..39 -> 1257
                in 40..59 -> 1258
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            14 -> when (minute) {
                in 0..19 -> 1259
                in 20..39 -> 1260
                in 40..59 -> 1261
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            15 -> when (minute) {
                in 0..19 -> 1262
                in 20..39 -> 1263
                in 40..59 -> 1264
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            16 -> when (minute) {
                in 0..19 -> 1265
                in 20..39 -> 1266
                in 40..59 -> 1267
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            17 -> when (minute) {
                in 0..19 -> 1268
                in 20..39 -> 1269
                in 40..59 -> 1270
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            18 -> when (minute) {
                in 0..19 -> 1271
                in 20..39 -> 1272
                in 40..59 -> 1273
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            19 -> when (minute) {
                in 0..19 -> 1274
                in 20..39 -> 1275
                in 40..59 -> 1276
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            20 -> when (minute) {
                in 0..19 -> 1277
                in 20..39 -> 1278
                in 40..59 -> 1279
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            21 -> when (minute) {
                in 0..19 -> 1280
                in 20..39 -> 1281
                else -> throw IndexOutOfBoundsException("Invalid minute")
            }
            else -> throw IndexOutOfBoundsException("Invalid hour")
        }
    }
}
