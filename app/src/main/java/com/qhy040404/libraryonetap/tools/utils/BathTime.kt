package com.qhy040404.libraryonetap.tools.utils

import java.util.*

object BathTime {
    fun getBathTime(): Int {
        val calendar: Calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        var timeid = 0

        when (hour) {
            9 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1244
                    }
                    in 20..39 -> {
                        timeid = 1245
                    }
                    in 40..59 -> {
                        timeid = 1246
                    }
                }
            }
            10 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1247
                    }
                    in 20..39 -> {
                        timeid = 1248
                    }
                    in 40..59 -> {
                        timeid = 1249
                    }
                }
            }
            11 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1250
                    }
                    in 20..39 -> {
                        timeid = 1251
                    }
                    in 40..59 -> {
                        timeid = 1252
                    }
                }
            }
            12 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1253
                    }
                    in 20..39 -> {
                        timeid = 1254
                    }
                    in 40..59 -> {
                        timeid = 1255
                    }
                }
            }
            13 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1256
                    }
                    in 20..39 -> {
                        timeid = 1257
                    }
                    in 40..59 -> {
                        timeid = 1258
                    }
                }
            }
            14 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1259
                    }
                    in 20..39 -> {
                        timeid = 1260
                    }
                    in 40..59 -> {
                        timeid = 1261
                    }
                }
            }
            15 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1262
                    }
                    in 20..39 -> {
                        timeid = 1263
                    }
                    in 40..59 -> {
                        timeid = 1264
                    }
                }
            }
            16 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1265
                    }
                    in 20..39 -> {
                        timeid = 1266
                    }
                    in 40..59 -> {
                        timeid = 1267
                    }
                }
            }
            17 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1268
                    }
                    in 20..39 -> {
                        timeid = 1269
                    }
                    in 40..59 -> {
                        timeid = 1270
                    }
                }
            }
            18 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1271
                    }
                    in 20..39 -> {
                        timeid = 1272
                    }
                    in 40..59 -> {
                        timeid = 1273
                    }
                }
            }
            19 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1274
                    }
                    in 20..39 -> {
                        timeid = 1275
                    }
                    in 40..59 -> {
                        timeid = 1276
                    }
                }
            }
            20 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1277
                    }
                    in 20..39 -> {
                        timeid = 1278
                    }
                    in 40..59 -> {
                        timeid = 1279
                    }
                }
            }
            21 -> {
                when (minute) {
                    in 0..19 -> {
                        timeid = 1280
                    }
                    in 20..39 -> {
                        timeid = 1281
                    }
                }
            }
        }
        return timeid
    }
}