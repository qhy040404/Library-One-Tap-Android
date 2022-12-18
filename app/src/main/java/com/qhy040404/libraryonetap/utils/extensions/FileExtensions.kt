package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.utils.HashUtils
import java.io.File

@Suppress("unused")
object FileExtensions {
    /**
     * Calculate a File's MD5 hash
     *
     * @return MD5 hash of the File
     */
    fun File.md5() = HashUtils.md5(this.readBytes())

    /**
     * Calculate a File's SHA-1 hash
     *
     * @return SHA-1 hash of the File
     */
    fun File.sha1() = HashUtils.sha1(this.readBytes())

    /**
     * Calculate a File's SHA-256 hash
     *
     * @return SHA-256 hash of the File
     */
    fun File.sha256() = HashUtils.sha256(this.readBytes())

    /**
     * Calculate a File's SHA-512 hash
     *
     * @return SHA-512 hash of the File
     */
    fun File.sha512() = HashUtils.sha512(this.readBytes())
}
