package com.towamwms.utils

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object HashUtil {
    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun hash(value: String): ByteArray {
        val sha256 = MessageDigest.getInstance("SHA-256")
        return sha256.digest(value.toByteArray(charset("UTF-8")))
    }
    @Throws(NoSuchAlgorithmException::class)
    fun hash(value: ByteArray): ByteArray {
        val sha256 = MessageDigest.getInstance("SHA-256")
        return sha256.digest(value)
    }
}
