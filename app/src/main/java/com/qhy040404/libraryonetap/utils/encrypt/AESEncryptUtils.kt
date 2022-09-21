package com.qhy040404.libraryonetap.utils.encrypt

import androidx.annotation.RawRes
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.runner.JsRunner
import okio.buffer
import okio.source

object AESEncryptUtils {
    var initialized = false

    fun init() {
        initRaw(R.raw.aes)
        initialized = true
    }

    private fun initRaw(@RawRes id: Int) {
        LibraryOneTapApp.app.resources.openRawResource(id).apply {
            JsRunner.initScript(source().buffer().readString(Charsets.UTF_8))
        }
    }
}
