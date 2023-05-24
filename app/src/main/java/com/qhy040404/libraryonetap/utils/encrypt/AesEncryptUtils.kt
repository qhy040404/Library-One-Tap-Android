package com.qhy040404.libraryonetap.utils.encrypt

import android.annotation.SuppressLint
import android.util.Base64
import com.qhy040404.libraryonetap.utils.extensions.toHex
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.security.Security
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

    @SuppressLint("GetInstance")
    object VCard {
        private const val vCardCipherMode = "AES/ECB/PKCS7Padding"

        init {
            Security.addProvider(BouncyCastleProvider())
        }

        fun genKey(template: Int = 16): String {
            val map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            return buildString {
                repeat(template) {
                    append(map[(Math.random() * map.length).toInt()])
                }
            }
        }

        fun encrypt(data: String, key: String): String {
            return Cipher.getInstance(vCardCipherMode, "SC").apply {
                init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES"))
            }.let { cipher ->
                Base64.encodeToString(
                    cipher.doFinal(data.toByteArray(Charsets.UTF_8)),
                    Base64.DEFAULT
                )
            }
        }

        fun decrypt(data: String, key: String): String {
            return Cipher.getInstance(vCardCipherMode, "SC").apply {
                init(Cipher.DECRYPT_MODE, SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES"))
            }.doFinal(Base64.decode(data, Base64.DEFAULT)).decodeToString()
        }

        fun handleKey(data: String, reversed: Boolean): String {
            return if (reversed) {
                (data.substring(10) + data.substring(0, 10)).reversed()
            } else {
                data.reversed().let {
                    it.substring(6) + it.substring(0, 6)
                }
            }
        }
    }
}
