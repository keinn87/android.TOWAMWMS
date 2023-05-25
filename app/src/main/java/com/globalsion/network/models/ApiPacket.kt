package com.globalsion.network.models

import android.text.TextUtils
import android.util.Log
import com.towamwms.Preferences
import com.globalsion.network.enums.EnumApiPacketResult
import com.globalsion.utils.GsonUtil
import com.globalsion.utils.IOUtil
import com.google.gson.annotations.SerializedName

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

@Suppress("MemberVisibilityCanBePrivate", "unused", "LiftReturnOrAssignment")
class ApiPacket<T> {
    @SerializedName("result")
    var result: EnumApiPacketResult = EnumApiPacketResult.FALSE
    var value: T? = null
    var message: String? = null
    var faulted: String? = null

    private fun pack(): ByteArrayOutputStream {
        try {
            val stream = ByteArrayOutputStream()
            if (doCompression()) {
                val gzip = GZIPOutputStream(stream)

                if (!logCompressionRate()) {
                    val writer = OutputStreamWriter(gzip, Charsets.UTF_8.name())
                    val gson = GsonUtil.gson

                    if (!logRawJson()) {
                        gson.toJson(this, writer)

                        writer.flush()
                        gzip.flush()
                        gzip.finish()
                    } else {
                        val json = gson.toJson(this)

                        Log.d(ApiPacket::class.java.name, "ApiPacket (Pack): $json")
                        writer.write(json)

                        writer.flush()
                        gzip.flush()
                        gzip.finish()
                    }

                } else {
                    val gson = GsonUtil.gson
                    val json = gson.toJson(this)

                    if (logRawJson()) {
                        Log.d(ApiPacket::class.java.name, "ApiPacket (Pack): $json")
                    }

                    val bJson = json.toByteArray(Charsets.UTF_8)
                    gzip.write(bJson)
                    gzip.flush()
                    gzip.finish()

                    val originalSize = BigDecimal.valueOf(bJson.size.toLong())
                    val compressedSize = BigDecimal.valueOf(stream
                            .size().toLong())
                    val compressionRate = compressedSize
                            .divide(originalSize, 10, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(8, RoundingMode.HALF_UP)

                    val format = "ApiPacket (Compression): " + "Rate [%s], Original [%s bytes], Compressed [%s bytes]"
                    Log.d(ApiPacket::class.java.name, String.format(format,
                            compressionRate.toString() + "%",
                            originalSize.toString(),
                            compressedSize.toString()))
                }
            } else {
                val writer = OutputStreamWriter(stream)
                val gson = GsonUtil.gson

                if (!logRawJson()) {
                    gson.toJson(this, writer)
                } else {
                    val json = gson.toJson(this)

                    Log.d(ApiPacket::class.java.name, "ApiPacket (Pack): $json")
                    writer.write(json)
                }
                writer.flush()
            }
            return stream
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    companion object {
        fun <T> pack(result: EnumApiPacketResult, value: T,
                     message: String?): ByteArrayOutputStream {
            val rest = ApiPacket<T>()
            rest.result = result
            rest.value = value
            rest.message = message

            return rest.pack()
        }

        fun <T> pack(result: EnumApiPacketResult, value: T,
                     message: String, faulted: String?): ByteArrayOutputStream {
            val rest = ApiPacket<T>()
            rest.result = result
            rest.value = value
            rest.message = message
            rest.faulted = faulted

            return rest.pack()
        }

        @Throws(IOException::class)
        fun <T> unpack(stream: InputStream, typeOfT: Type): ApiPacket<T> {
            val reader: InputStreamReader

            if (doCompression()) {
                val gzip = GZIPInputStream(stream)
                reader = InputStreamReader(gzip, Charsets.UTF_8.name())
            } else {
                reader = InputStreamReader(stream, Charsets.UTF_8.name())
            }

            val gson = GsonUtil.gson
            if (!logRawJson()) {
                return gson.fromJson(reader, typeOfT)
            } else {
                var json = IOUtil.toString(reader)

                if (!TextUtils.isEmpty(json) && json[0] == (-257).toChar()) {
                    json = json.substring(1)
                }

                Log.d(ApiPacket::class.java.name, "ApiPacket (Unpack): $json")
                return gson.fromJson(json, typeOfT)
            }
        }

        private fun doCompression(): Boolean {
            return Preferences.getInstance()
                    .getBoolean(Preferences.API_PACKET_COMPRESSION, false)
        }

        private fun logCompressionRate(): Boolean {
            return Preferences.getInstance()
                    .getBoolean(Preferences.API_PACKET_LOG_COMPRESSION_RATE, false)
        }

        private fun logRawJson(): Boolean {
            return Preferences.getInstance()
                    .getBoolean(Preferences.API_PACKET_LOG_RAW_JSON, false)
        }
    }
}
