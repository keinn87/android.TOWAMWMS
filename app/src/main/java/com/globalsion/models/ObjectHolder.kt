package com.globalsion.models

import com.globalsion.network.enums.EnumObjectType
import com.globalsion.utils.ClassUtil
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type
import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import com.globalsion.network.utils.SerializedTypeNameUtil

class ObjectHolder(value: Any?) {
    var value: Any? = null
        set(value) {
            @Suppress("USELESS_IS_CHECK")
            if (value == null) {
                objectType = EnumObjectType.NULL
            } else if (value is Char) {
                objectType = EnumObjectType.CHARACTER
            } else if (value is Byte) {
                objectType = EnumObjectType.BYTE
            } else if (value is Short) {
                objectType = EnumObjectType.SHORT
            } else if (value is Int) {
                objectType = EnumObjectType.INTEGER
            } else if (value is Long) {
                objectType = EnumObjectType.LONG
            } else if (value is Float) {
                objectType = EnumObjectType.FLOAT
            } else if (value is Double) {
                objectType = EnumObjectType.DOUBLE
            } else if (value is String) {
                objectType = EnumObjectType.STRING
            } else if (value is Date) {
                objectType = EnumObjectType.DATE
            } else if (value is BigDecimal) {
                objectType = EnumObjectType.BIG_DECIMAL
            } else if (value is UUID) {
                objectType = EnumObjectType.UUID
            } else if (value is ObjectHolder) {
                objectType = EnumObjectType.OBJECT_HOLDER
            } else if (value is CharArray) {
                objectType = EnumObjectType.CHARACTER_ARRAY
            } else if (value is ByteArray) {
                objectType = EnumObjectType.BYTE_ARRAY
            } else if (value is ShortArray) {
                objectType = EnumObjectType.SHORT_ARRAY
            } else if (value is IntArray) {
                objectType = EnumObjectType.INTEGER_ARRAY
            } else if (value is LongArray) {
                objectType = EnumObjectType.LONG_ARRAY
            } else if (value is FloatArray) {
                objectType = EnumObjectType.FLOAT_ARRAY
            } else if (value is DoubleArray) {
                objectType = EnumObjectType.DOUBLE_ARRAY
            } else if (value is Array<*> && value.isArrayOf<String>()) {
                objectType = EnumObjectType.STRING_ARRAY
            } else if (value is Array<*> && value.isArrayOf<Date>()) {
                objectType = EnumObjectType.DATE_ARRAY
            } else if (value is Array<*> && value.isArrayOf<BigDecimal>()) {
                objectType = EnumObjectType.BIG_DECIMAL_ARRAY
            } else if (value is Array<*> && value.isArrayOf<UUID>()) {
                objectType = EnumObjectType.UUID_ARRAY
            } else if (value is Array<*> && value.isArrayOf<ObjectHolder>()) {
                objectType = EnumObjectType.OBJECT_HOLDER_ARRAY
            } else if (value is Any) {
                objectType = EnumObjectType.OBJECT

                val typeNames = LinkedHashSet<String>()
                typeNames.add(value.javaClass.name)
                typeNames.addAll(SerializedTypeNameUtil.getTypeNames(value.javaClass).orEmpty().toList())

                this.typeNames = typeNames.toTypedArray()
            } else {
                throw UnsupportedOperationException("Not supported.")
            }

            field = value
        }

    var objectType: EnumObjectType? = null
        private set
    private var typeNames: Array<String>? = null

    init {
        this.value = value
    }

    class Adapter : JsonSerializer<ObjectHolder>, JsonDeserializer<ObjectHolder> {
        override fun serialize(src: ObjectHolder, typeOfSrc: Type,
                               context: JsonSerializationContext): JsonElement {
            val jobject = JsonObject()
            jobject.add("value", context.serialize(src.value))
            jobject.add("type", context.serialize(src.objectType!!.value))

            if (src.typeNames != null) {
                jobject.add("typeNames", context.serialize(src.typeNames))
            }
            return jobject
        }

        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type,
                                 context: JsonDeserializationContext): ObjectHolder {
            val objectHolder = ObjectHolder(null)
            val jobject = json as JsonObject

            val type: EnumObjectType? = context.deserialize(jobject.get("type"), EnumObjectType::class.java)

            val elementValue = jobject.get("value")
            val value: Any?

            if (type == null) {
                throw JsonParseException("Invalid object holder, type is not specified.")
            } else {
                if (type === EnumObjectType.NULL) {
                    value = null
                } else if (type === EnumObjectType.CHARACTER) {
                    value = context.deserialize(elementValue, Char::class.java)
                } else if (type === EnumObjectType.BYTE) {
                    value = context.deserialize(elementValue, Byte::class.java)
                } else if (type === EnumObjectType.SHORT) {
                    value = context.deserialize(elementValue, Short::class.java)
                } else if (type === EnumObjectType.INTEGER) {
                    value = context.deserialize(elementValue, Int::class.java)
                } else if (type === EnumObjectType.LONG) {
                    value = context.deserialize(elementValue, Long::class.java)
                } else if (type === EnumObjectType.FLOAT) {
                    value = context.deserialize(elementValue, Float::class.java)
                } else if (type === EnumObjectType.DOUBLE) {
                    value = context.deserialize(elementValue, Double::class.java)
                } else if (type === EnumObjectType.STRING) {
                    value = context.deserialize(elementValue, String::class.java)
                } else if (type === EnumObjectType.DATE) {
                    value = context.deserialize(elementValue, Date::class.java)
                } else if (type === EnumObjectType.BIG_DECIMAL) {
                    value = context.deserialize(elementValue, BigDecimal::class.java)
                } else if (type === EnumObjectType.UUID) {
                    value = context.deserialize(elementValue, UUID::class.java)
                } else if (type === EnumObjectType.OBJECT_HOLDER) {
                    value = context.deserialize(elementValue, ObjectHolder::class.java)
                } else if (type === EnumObjectType.CHARACTER_ARRAY) {
                    value = context.deserialize(elementValue, CharArray::class.java)
                } else if (type === EnumObjectType.BYTE_ARRAY) {
                    value = context.deserialize(elementValue, ByteArray::class.java)
                } else if (type === EnumObjectType.SHORT_ARRAY) {
                    value = context.deserialize(elementValue, ShortArray::class.java)
                } else if (type === EnumObjectType.INTEGER_ARRAY) {
                    value = context.deserialize(elementValue, IntArray::class.java)
                } else if (type === EnumObjectType.LONG_ARRAY) {
                    value = context.deserialize(elementValue, LongArray::class.java)
                } else if (type === EnumObjectType.FLOAT_ARRAY) {
                    value = context.deserialize(elementValue, FloatArray::class.java)
                } else if (type === EnumObjectType.DOUBLE_ARRAY) {
                    value = context.deserialize(elementValue, DoubleArray::class.java)
                } else if (type === EnumObjectType.DATE_ARRAY) {
                    value = context.deserialize(elementValue, Array<Date>::class.java)
                } else if (type === EnumObjectType.STRING_ARRAY) {
                    value = context.deserialize(elementValue, Array<String>::class.java)
                } else if (type === EnumObjectType.BIG_DECIMAL_ARRAY) {
                    value = context.deserialize(elementValue, Array<BigDecimal>::class.java)
                } else if (type === EnumObjectType.UUID_ARRAY) {
                    value = context.deserialize(elementValue, Array<UUID>::class.java)
                } else if (type === EnumObjectType.OBJECT_HOLDER_ARRAY) {
                    value = context.deserialize(elementValue, Array<ObjectHolder>::class.java)
                } else if (type === EnumObjectType.OBJECT) {
                    val elementTemp = jobject.get("typeNames")
                    if (elementTemp == null || elementTemp.isJsonNull) {
                        throw JsonParseException("Invalid object holder, typeNames is not specified.")
                    }

                    val typeNames: Array<String> = context.deserialize(elementTemp, Array<String>::class.java)
                    if (typeNames.isEmpty()) {
                        throw JsonParseException("Invalid object holder, typeNames is not specified.")
                    }

                    var found: Class<*>? = null
                    for (typeName in typeNames) {

                        try {
                            found = ClassUtil.forName(typeName)
                        } catch (e: ClassNotFoundException) {
                            //ignore
                        }
                    }

                    if (found == null) {
                        throw JsonParseException("Invalid object holder, no valid typeName found.")
                    }

                    value = context.deserialize(elementValue, found)
                } else {
                    throw JsonParseException("Invalid object holder, unknown objectType.")
                }
            }

            objectHolder.value = value
            objectHolder.objectType = type

            return objectHolder
        }
    }
}
