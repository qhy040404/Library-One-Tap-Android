package com.qhy040404.libraryonetap

import com.qhy040404.libraryonetap.utils.des.DesEncryptUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DesTest {
    @Test
    fun testEncAndDec() {
        val des = DesEncryptUtils()
        val testString = "ThisIsATest"
        assertEquals(testString, des.strDec(des.strEnc(testString, "1", "2", "3"), "1", "2", "3"))
    }
}
