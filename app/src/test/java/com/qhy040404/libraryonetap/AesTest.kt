package com.qhy040404.libraryonetap

import com.qhy040404.libraryonetap.utils.web.WebVPNUtils
import org.junit.Assert
import org.junit.Test

class AesTest {
    @Test
    fun test() {
        Assert.assertEquals(
            "/http/77726476706e69737468656265737421e4f2409f2f7e6c5c6b1cc7a99c406d3690/",
            WebVPNUtils.encryptUrl("http://teach.dlut.edu.cn/")
        )
        Assert.assertEquals(
            "/https/77726476706e69737468656265737421e7e056d2233c7d44300d8db9d6562d/",
            WebVPNUtils.encryptUrl("https://www.dlut.edu.cn/")
        )
    }
}
