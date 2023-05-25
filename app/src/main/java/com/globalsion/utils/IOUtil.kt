package com.globalsion.utils

import java.io.*
import java.nio.charset.Charset

@Suppress("MemberVisibilityCanBePrivate")
object IOUtil {
    /**
     * Close without throwing exception.
     **/
    fun closeQuietly(closeable: Closeable?): IOException? {
        try {
            closeable?.close()
        } catch (e: IOException) {
            return e
        }

        return null
    }

    /**
     * Convert stream to string.
     **/
    fun toString(input: InputStream, charset: Charset): String {
        var reader: Reader? = null

        try {
            reader = InputStreamReader(input, charset)
            return toString(reader)
        } catch (e: Exception) {
            throw e
        } finally {
            closeQuietly(reader)
        }
    }

    /**
     * Read reader string read to end.
     **/
    fun toString(input: Reader): String {
        val builder = StringBuilder()
        val buffer = CharArray(1024)
        var read = 0

        while ({ read = input.read(buffer, 0, buffer.size); read }() != -1) {
            builder.append(buffer, 0, read)
        }

        return builder.toString()
    }
}