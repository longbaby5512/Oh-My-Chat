package com.karry.ohmychat.utils

import java.security.MessageDigest

object Hash {
    fun getHash(plaintext: String, algorithm: String? = null): String {
        val bytes =
            MessageDigest.getInstance(algorithm ?: "SHA-256").digest(plaintext.toByteArray())
        return toHex(bytes)
    }

    private fun toHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
}