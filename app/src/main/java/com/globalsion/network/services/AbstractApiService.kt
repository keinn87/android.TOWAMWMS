package com.globalsion.network.services

import android.text.TextUtils
import com.globalsion.models.ObjectHolder
import com.globalsion.network.exceptions.ApiPacketException
import com.globalsion.network.models.ApiPacket
import com.globalsion.network.enums.EnumApiPacketResult
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection
import java.net.URL

abstract class AbstractApiService(rootUrl: String) {
    companion object {
        const val REQUEST_METHOD = "POST"
    }

    var rootUrl: String

    init {
        if (TextUtils.isEmpty(rootUrl)) {
            throw IllegalArgumentException("rootUrl")
        }

        this.rootUrl = rootUrl
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun concatUrl(url: String, page: String): String {
        return if (url.endsWith("/")) {
            url + page
        } else {
            "$url/$page"
        }
    }

    /**
     * Request to the server and get the result.
     **/
    protected inline fun <reified T> request(page: String, value: Any?): ApiPacket<T> {
        val url = concatUrl(rootUrl, page)

        var connection: HttpURLConnection? = null

        try {
            connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = REQUEST_METHOD
            connection.doInput = true
            connection.doOutput = true

            ApiPacket.pack(EnumApiPacketResult.TRUE, value, null)
                    .writeTo(connection.outputStream)

            val token = TypeToken.getParameterized(ApiPacket::class.java, T::class.java)
            val packet = ApiPacket.unpack<T>(connection.inputStream, token.type)

            return when {
                packet.result == EnumApiPacketResult.FAULTED -> throw ApiPacketException(packet.faulted)
                packet.result == EnumApiPacketResult.FALSE -> throw ApiPacketException(packet.message)
                else -> packet
            }
        } finally {
            connection?.disconnect()
        }
    }

    protected fun convertHashMap(map: HashMap<String, Any?>?) : HashMap<String, ObjectHolder>? {
        if (map == null) return null

        val temp = HashMap<String, ObjectHolder>()

        for (entry in map.entries) {
            temp[entry.key] = ObjectHolder(entry.value)
        }

        return temp
    }
}