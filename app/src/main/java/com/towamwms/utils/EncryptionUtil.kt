package com.towamwms.utils

import android.annotation.SuppressLint
import android.util.Base64

import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.Arrays

import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Suppress("unused")
object EncryptionUtil {
    private const val seedRandom = 0x23f1345773abab37L

    var keyBytes: ByteArray? = null
        private set
    var keyString: String? = null
        private set

    init {
        keyString = "Fate/Lancer"
        try {
            keyBytes = keyString!!.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }

    }

    /**
     * Encrypt data using AES algorithm
     * @param key Key to use for encryption, null to use default key.
     * @param plain Data to encrypt
     * @return Return encrypted data
     */
    @SuppressLint("SecureRandom")
    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class, InvalidAlgorithmParameterException::class, NoSuchPaddingException::class, IllegalBlockSizeException::class, BadPaddingException::class)
    fun encrypt(key: ByteArray?, plain: ByteArray): ByteArray {
        val tmpKey = key ?: keyBytes

        val random = SecureRandom.getInstance("SHA1PRNG")
        val sha256 = MessageDigest.getInstance("SHA-256")

        var digested = sha256.digest(tmpKey)
        digested = Arrays.copyOf(digested, 16)

        val skeySpec = SecretKeySpec(digested, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val ivBytes = ByteArray(cipher.blockSize)
        random.setSeed(seedRandom)
        random.nextBytes(ivBytes)
        val iv = IvParameterSpec(ivBytes)
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)

        return cipher.doFinal(plain)
    }

    /**
     * Encrypt string using AES algorithm.
     * @param key Key to use for encryption, null to use default key.
     * @param plain String to encrypt.
     * @return Return encrypted string.
     */
    @Throws(UnsupportedEncodingException::class, InvalidKeyException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, NoSuchPaddingException::class, IllegalBlockSizeException::class, BadPaddingException::class)
    fun encrypt(key: String?, plain: String): String {

        val keyBytes = key?.toByteArray(charset("UTF-8")) ?: keyBytes
        val plainBytes = plain.toByteArray(charset("UTF-8"))

        return Base64.encodeToString(encrypt(keyBytes, plainBytes), Base64.DEFAULT)
    }

    /**
     * Decrypt data using AES algorithm.
     * @param key Key to use for decryption, null to use default key.
     * @param encrypted Data to decrypt.
     * @return Return decrypted data.
     */
    @SuppressLint("SecureRandom")
    @Throws(IllegalBlockSizeException::class, BadPaddingException::class, InvalidKeyException::class, InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class)
    fun decrypt(key: ByteArray?, encrypted: ByteArray): ByteArray {
        val tmpKey = key ?: keyBytes

        val random = SecureRandom.getInstance("SHA1PRNG")
        val sha256 = MessageDigest.getInstance("SHA-256")

        var digested = sha256.digest(tmpKey)
        digested = Arrays.copyOf(digested, 16)
        val skeySpec = SecretKeySpec(digested, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val ivBytes = ByteArray(cipher.blockSize)
        random.setSeed(seedRandom)
        random.nextBytes(ivBytes)
        val iv = IvParameterSpec(ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)

        return cipher.doFinal(encrypted)
    }

    /**
     * Decrypt string using AES algorithm.
     * @param key Key to use for decryption, null to use default key.
     * @param encrypted String to decrypt.
     * @return Return decrypted string.
     */
    @Throws(InvalidKeyException::class, UnsupportedEncodingException::class, IllegalBlockSizeException::class, BadPaddingException::class, InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class)
    fun decrypt(key: String?, encrypted: String): String {

        val keyBytes = key?.toByteArray(Charsets.UTF_8) ?: keyBytes
        val encryptedBytes = Base64.decode(encrypted, Base64.DEFAULT)

        return String(decrypt(keyBytes, encryptedBytes), Charsets.UTF_8)
    }
}
