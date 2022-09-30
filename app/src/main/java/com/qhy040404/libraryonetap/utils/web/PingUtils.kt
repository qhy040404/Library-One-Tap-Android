package com.qhy040404.libraryonetap.utils.web

import com.qhy040404.libraryonetap.utils.extensions.StringExtension.exec
import kotlin.math.roundToInt

object PingUtils {
    private const val TIME_OUT = 3
    private const val DEFAULT_COUNT = 1

    private const val TYPE_MIN = 1
    private const val TYPE_AVG = 2
    private const val TYPE_MAX = 3
    private const val TYPE_MDEV = 4

    /**
     * Network state
     *
     * @param domain
     * @return Boolean
     */
    fun parseRTT(domain: String): Boolean {
        val pingRtt = getRTT(domain, count = 3)
        val loss = getPacketLoss(domain, count = 3)
        return pingRtt in 1 until 200 || loss == 0F
    }

    /**
     * Get RTT
     *
     * @param domain
     * @param type      Ping Type
     * @param count     Ping times
     * @param timeout   Ping timeout
     * @return RTT (-1 as default, which means failure)
     */
    private fun getRTT(
        domain: String,
        type: Int = TYPE_AVG,
        count: Int = DEFAULT_COUNT,
        timeout: Int = TIME_OUT,
    ): Int {
        val result = generatePingParams(count, timeout, domain).exec()
        return runCatching {
            val tempInfo = result.substring(result.indexOf("min/avg/max/mdev") + 19)
            val temps = tempInfo.split("/")
            when (type) {
                TYPE_MIN -> temps[0].toFloat().roundToInt()
                TYPE_AVG -> temps[1].toFloat().roundToInt()
                TYPE_MAX -> temps[2].toFloat().roundToInt()
                TYPE_MDEV -> temps[3].toFloat().roundToInt()
                else -> -1
            }
        }.getOrDefault(-1)
    }

    /**
     * Get Loss Percentage
     *
     * @param domain
     * @param count     Ping times
     * @param timeout   Ping timeout
     * @return Loss Float
     */
    private fun getPacketLoss(
        domain: String,
        count: Int = DEFAULT_COUNT,
        timeout: Int = TIME_OUT,
    ): Float {
        val result = generatePingParams(count, timeout, domain).exec()
        return runCatching {
            val temp = result.substring(result.indexOf("received,"))
            temp.substring(9, temp.indexOf("packet")).replace("%", "").toFloat()
        }.getOrDefault(100F)
    }

    /**
     * Generate Ping params
     *
     * @param count
     * @param timeout
     * @param domain
     * @return String
     */
    private fun generatePingParams(count: Int, timeout: Int, domain: String): String =
        "ping -c $count -w $timeout $domain"
}
