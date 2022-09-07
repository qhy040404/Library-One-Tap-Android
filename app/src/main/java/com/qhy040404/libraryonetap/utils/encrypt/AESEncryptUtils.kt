package com.qhy040404.libraryonetap.utils.encrypt

import androidx.annotation.RawRes
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.runner.JsRunner
import org.apache.http.util.EncodingUtils

object AESEncryptUtils {
    fun init() {
        initRaw(R.raw.aes)
        initRaw(R.raw.aesencoder)
    }

    private fun initRaw(@RawRes id: Int) {
        LibraryOneTapApp.app.resources.openRawResource(id).apply {
            val length = available()
            val buffer = ByteArray(length)
            read(buffer)
            JsRunner.initScript(EncodingUtils.getString(buffer, "utf-8"))
        }
    }
}
