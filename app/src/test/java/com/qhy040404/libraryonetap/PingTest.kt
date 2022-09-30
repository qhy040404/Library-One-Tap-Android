package com.qhy040404.libraryonetap

import com.qhy040404.libraryonetap.utils.web.PingUtils
import org.junit.Assert.assertTrue
import org.junit.Test

class PingTest {
    @Test
    fun pingBaidu() {
        println(PingUtils.parseRTT("baidu.com"))
        assertTrue(PingUtils.parseRTT("baidu.com"))
    }
}
