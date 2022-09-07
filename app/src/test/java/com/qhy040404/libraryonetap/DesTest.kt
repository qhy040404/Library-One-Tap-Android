package com.qhy040404.libraryonetap

import com.qhy040404.libraryonetap.utils.des.DesEncryptUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DesTest {
    @Test
    fun testEncAndDec() {
        val des = DesEncryptUtils()
        val testStringA = "ThisIsATest"
        val testStringB = "AnotherKeyTest"
        val testStringC = "LongKeyTest"
        assertEquals(testStringA, des.strDec(des.strEnc(testStringA, "1", "2", "3"), "1", "2", "3"))
        assertEquals(testStringB,
            des.strDec(des.strEnc(testStringB, "a", "b", "cc"), "a", "b", "cc"))
        assertEquals(testStringC,
            des.strDec(des.strEnc(testStringC, "a", "little", "longer"), "a", "little", "longer"))
    }
}
