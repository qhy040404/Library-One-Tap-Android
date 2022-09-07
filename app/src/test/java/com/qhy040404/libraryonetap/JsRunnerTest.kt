package com.qhy040404.libraryonetap

import com.qhy040404.libraryonetap.runner.JsRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JsRunnerTest {
    @Test
    fun testJs() {
        var js = "function aa(){return 'testA';}"
        JsRunner.initScript(js)
        js = "function bb(){return 'testB';}"
        JsRunner.initScript(js)

        assertEquals("testA", JsRunner.callFunc("aa"))
        assertEquals("testB", JsRunner.callFunc("bb"))
    }
}
