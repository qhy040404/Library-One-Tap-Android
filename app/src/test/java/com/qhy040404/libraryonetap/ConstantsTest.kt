package com.qhy040404.libraryonetap

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ConstantsTest {
    @Test
    fun hasBuglyAppId() {
        assertNotNull(BuildConfig.BUGLY_APPID)
        assertNotEquals("", BuildConfig.BUGLY_APPID)
    }
}
