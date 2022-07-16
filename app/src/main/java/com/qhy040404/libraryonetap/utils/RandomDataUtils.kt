package com.qhy040404.libraryonetap.utils

import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.utils.lazy.ResettableLazyUtils
import java.util.*

object RandomDataUtils {
    fun getNum(numCount: Int): Int {
        return if (numCount > 0) {
            Random().nextInt(numCount)
        } else {
            0
        }
    }

    val randomTheme by ResettableLazyUtils.resettableLazy(GlobalManager.lazyMgr) {
        when (getNum(7)) {
            0 -> "purple"
            1 -> "blue"
            2 -> "pink"
            3 -> "green"
            4 -> "orange"
            5 -> "red"
            else -> "simple"
        }
    }
}