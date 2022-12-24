package com.qhy040404.libraryonetap

import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DesTest {
    @Test
    fun testEncAndDec() {
        val testStringA = "ThisIsATest"
        val testStringB = "AnotherKeyTest"
        val testStringC = "LongKeyTest"
        assertEquals(testStringA,
            DesEncryptUtils.strDec(DesEncryptUtils.strEnc(testStringA, "1", "2", "3"),
                "1",
                "2",
                "3"))
        assertEquals(testStringB,
            DesEncryptUtils.strDec(DesEncryptUtils.strEnc(testStringB, "a", "b", "cc"),
                "a",
                "b",
                "cc"))
        assertEquals(testStringC,
            DesEncryptUtils.strDec(DesEncryptUtils.strEnc(testStringC, "a", "little", "longer"),
                "a",
                "little",
                "longer"))
    }
}
