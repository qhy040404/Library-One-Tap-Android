package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.utils.HashUtils

@Suppress("unused")
object ByteExtensions {
    /**
     * Calculate a ByteArray's MD5 hash
     *
     * @return MD5 hash of the ByteArray
     */
    fun ByteArray.md5() = HashUtils.md5(this)

    /**
     * Calculate a ByteArray's SHA-1 hash
     *
     * @return SHA-1 hash of the ByteArray
     */
    fun ByteArray.sha1() = HashUtils.sha1(this)

    /**
     * Calculate a ByteArray's SHA-256 hash
     *
     * @return SHA-256 hash of the ByteArray
     */
    fun ByteArray.sha256() = HashUtils.sha256(this)

    /**
     * Calculate a ByteArray's SHA-512 hash
     *
     * @return SHA-512 hash of the ByteArray
     */
    fun ByteArray.sha512() = HashUtils.sha512(this)

    /**
     * Convert a ByteArray to Hex String
     *
     * @return HexString of the ByteArray
     */
    fun ByteArray.toHex(): String {
        return buildString {
            this@toHex.forEach {
                var hex = Integer.toHexString(it.toInt() and 0xFF)
                if (hex.length == 1) {
                    hex = "0$hex"
                }
                append(hex.lowercase())
            }
        }
    }
}
