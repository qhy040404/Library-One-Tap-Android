package com.qhy040404.libraryonetap.utils.extensions

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Keep two decimal places after rounding
 *
 * @return Double with two decimal
 */
fun Double.to2Decimal(): Double {
  return runCatching {
    BigDecimal.valueOf(this).setScale(2, RoundingMode.HALF_UP).toDouble()
  }.getOrDefault(0.0)
}
