package com.qhy040404.libraryonetap

import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import org.junit.Assert
import org.junit.Test

class DesTest {
    @Test
    fun testEncAndDec() {
        val testStringA = "ThisIsATest"
        val testStringB = "AnotherKeyTest"
        val testStringC = "LongKeyTest"
        val testString = "ConstantTest"
        Assert.assertEquals(
            testStringA,
            DesEncryptUtils.strDec(
                DesEncryptUtils.strEnc(testStringA, "1", "2", "3"),
                "1",
                "2",
                "3"
            )
        )
        Assert.assertEquals(
            testStringB,
            DesEncryptUtils.strDec(
                DesEncryptUtils.strEnc(testStringB, "a", "b", "cc"),
                "a",
                "b",
                "cc"
            )
        )
        Assert.assertEquals(
            testStringC,
            DesEncryptUtils.strDec(
                DesEncryptUtils.strEnc(testStringC, "a", "little", "longer"),
                "a",
                "little",
                "longer"
            )
        )
        Assert.assertEquals(
            "BBDED83F263993C4250BD1B81A688702C0A5C6CA237A670E",
            DesEncryptUtils.strEnc(testString, "1", "2", "3")
        )
    }
}
