package com.qhy040404.libraryonetap.utils

import java.util.*


object RandomDataUtils {
    fun getNum(endNum: Int): Int {
        return if (endNum > 0) {
            Random().nextInt(endNum)
        } else {
            0
        }
    }
}