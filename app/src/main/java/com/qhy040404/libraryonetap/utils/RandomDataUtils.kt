package com.qhy040404.libraryonetap.utils

import java.util.*

object RandomDataUtils {
    fun getNum(numCount: Int): Int {
        return if (numCount > 0) {
            Random().nextInt(numCount)
        } else {
            0
        }
    }
}