package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.utils.HashUtils
import java.io.File

@Suppress("unused")
object FileExtensions {
    fun File.md5() = HashUtils.md5(this.readBytes())

    fun File.sha1() = HashUtils.sha1(this.readBytes())

    fun File.sha256() = HashUtils.sha256(this.readBytes())

    fun File.sha512() = HashUtils.sha512(this.readBytes())
}
