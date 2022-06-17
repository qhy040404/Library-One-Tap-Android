package com.qhy040404.libraryonetap.utils

import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.utils.lazy.ResettableLazyUtils
import java.util.*

object RandomDataUtils {
    fun getNum(endNum: Int): Int {
        return if (endNum > 0) {
            Random().nextInt(endNum)
        } else {
            0
        }
    }

    val randomTheme by ResettableLazyUtils.resettableLazy(GlobalManager.lazyMgr) {
        when (getNum(4)) {
            0 -> "purple"
            1 -> "blue"
            2 -> "pink"
            3 -> "green"
            else -> "simple"
        }
    }
}