package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.utils.HashUtils

@Suppress("unused")
object ByteExtensions {
    fun ByteArray.md5() = HashUtils.md5(this)

    fun ByteArray.sha1() = HashUtils.sha1(this)

    fun ByteArray.sha256() = HashUtils.sha256(this)

    fun ByteArray.sha512() = HashUtils.sha512(this)
}
