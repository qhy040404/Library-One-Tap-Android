package com.qhy040404.libraryonetap.utils

import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.utils.lazy.resettableLazy
import java.util.Random

object RandomDataUtils {
  fun getNum(numCount: Int) = if (numCount > 0) {
    Random().nextInt(numCount)
  } else {
    0
  }

  val randomTheme by resettableLazy(GlobalManager.lazyMgr) {
    when (getNum(8)) {
      0 -> "purple"
      1 -> "library"
      2 -> "blue"
      3 -> "pink"
      4 -> "green"
      5 -> "orange"
      6 -> "red"
      else -> "simple"
    }
  }
}
