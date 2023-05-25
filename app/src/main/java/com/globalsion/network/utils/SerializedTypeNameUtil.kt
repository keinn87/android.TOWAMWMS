package com.globalsion.network.utils

import com.globalsion.network.annotations.SerializedTypeName

/**
 * Internal process for serialize and deserialize correct object
 */
object SerializedTypeNameUtil {
    fun getTypeNames(clazz: Class<*>?): Array<String>? {
        if (clazz == null) {
            throw IllegalArgumentException("clazz is null")
        }

        val serialized = clazz.getAnnotation(SerializedTypeName::class.java)
        return serialized?.value
    }
}