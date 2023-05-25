package com.towamwms.utils

import android.os.Parcel
import java.io.File

object ActivityUtil {
    var cacheDirectory : String = ""

    @Suppress("LiftReturnOrAssignment")
    fun cacheSave(fileName: String, any: Any) {
        createCacheDirectory()

        val file: File
        if (!cacheDirectory.isEmpty()) {
            file = File(cacheDirectory, fileName)
        } else {
            file = File(fileName)
        }
        if (file.exists()) {
            file.delete()
        }

        file.outputStream().use {
            val parcel = Parcel.obtain()
            try {
                parcel.writeValue(any)
                parcel.setDataPosition(0)
                it.write(parcel.marshall())
            } finally {
                parcel.recycle()
            }
        }
    }

    @Suppress("UNCHECKED_CAST", "LiftReturnOrAssignment")
    fun <T> cacheLoad(fileName: String) : T {
        createCacheDirectory()

        val file: File
        if (!cacheDirectory.isEmpty()) {
            file = File(cacheDirectory, fileName)
        } else {
            file = File(fileName)
        }

        file.inputStream().use {
            val parcel = Parcel.obtain()
            try {
                val data = it.readBytes(10 * 1024)
                parcel.unmarshall(data, 0, data.size)
                parcel.setDataPosition(0)
                return parcel.readValue(javaClass.classLoader) as T
            } finally {
                parcel.recycle()
            }
        }
    }

    private fun createCacheDirectory() {
        if (!cacheDirectory.isEmpty()) {
            val directory = File(cacheDirectory)
            if (!directory.exists()) {
                directory.mkdir()
            }
        }
    }
}