package com.globalsion.utils

import com.globalsion.models.ObjectHolder
import com.google.gson.*
import java.lang.ref.SoftReference
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import com.globalsion.models.PredicateParams
import com.google.gson.JsonElement
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer



object GsonUtil {
    /**
     * Cached gson object.
     * @see SoftReference
     **/
    private var softGson: SoftReference<Gson>? = null
    /**
     * Get gson object from cached or instantiate new gson object..
     **/
    val gson: Gson
        @Synchronized
        get() {
            var gson: Gson? = null
            if ({ gson = softGson?.get(); gson }() == null) {
                val builder = GsonBuilder()
                builder.registerTypeAdapter(ObjectHolder::class.java, ObjectHolder.Adapter())
                builder.registerTypeAdapter(PredicateParams::class.java, PredicateParamsAdapter())
                builder.registerTypeAdapter(Date::class.java, DateAdapter())
                //builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                gson = builder.create()
                val dateAdapter = gson!!.getAdapter(Date::class.java).nullSafe()
                builder.registerTypeAdapter(Date::class.java, dateAdapter)

                gson = builder.create()
                softGson = SoftReference(gson!!)
            }
            return gson!!
        }

    /**
     * Internal use, date format when serializing date object.
     **/
    class DateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

        private val simpleDateFormat: SimpleDateFormat
            get() {
                var dateFormat: SimpleDateFormat? = null
                if ({ dateFormat = softDateFormat?.get(); dateFormat }() == null) {
                    dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    dateFormat!!.timeZone = TimeZone.getTimeZone("UTC")
                    softDateFormat = SoftReference(dateFormat!!)
                }

                return dateFormat!!
            }

        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
            try {
                synchronized(locker) {
                    return simpleDateFormat.parse(json.asString)
                }
            } catch (e: ParseException) {
                throw JsonParseException(e)
            }
        }

        override fun serialize(src: Date, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            synchronized(locker) {
                return JsonPrimitive(simpleDateFormat.format(src))
            }
        }

        companion object {
            private var softDateFormat: SoftReference<SimpleDateFormat>? = null
            private val locker: Any = Any()
        }
    }

    /**
     * Internal use, serialization logic on [PredicateParams]
     **/
    class PredicateParamsAdapter : JsonSerializer<PredicateParams>, JsonDeserializer<PredicateParams> {
        override fun serialize(src: PredicateParams, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val holders = ArrayList<ObjectHolder>()

            val jobject = JsonObject()
            for (o in src.objects) {
                holders.add(ObjectHolder(o))
            }

            jobject.add("objects", context.serialize(holders))
            return jobject
        }

        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PredicateParams {
            val objects = ArrayList<Any?>()

            val jobject = json as JsonObject
            val jarray = jobject.get("objects") as JsonArray

            for (e in jarray) {
                val holder: ObjectHolder = context.deserialize(e, ObjectHolder::class.java)
                objects.add(holder.value)
            }
            return PredicateParams(objects.toTypedArray())
        }
    }
}