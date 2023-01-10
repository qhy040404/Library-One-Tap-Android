package com.qhy040404.libraryonetap.utils.encrypt

import com.qhy040404.libraryonetap.utils.extensions.ByteExtensions.toHex
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesEncryptUtils {
    private const val cipherMode = "AES/CFB/NoPadding"

    fun encrypt(text: String, key: String, iv: String): String {
        val sKeySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance(cipherMode)
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, IvParameterSpec(iv.toByteArray()))
        return cipher.doFinal(text.toByteArray()).toHex()
    }

    fun vpnEncrypt(text: String, key: String, iv: String): String {
        return iv.toByteArray().toHex() + encrypt(text, key, iv)
    }
}
