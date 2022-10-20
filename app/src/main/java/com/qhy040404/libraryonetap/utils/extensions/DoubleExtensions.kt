package com.qhy040404.libraryonetap.utils.extensions

import java.math.BigDecimal
import java.math.RoundingMode

object DoubleExtensions {
    /**
     * Keep two decimal places after rounding
     *
     * @return Double with two decimal
     */
    fun Double.to2Decimal() = BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()
}
