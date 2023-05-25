package com.globalsion.annotations.processors

import android.os.Bundle
import android.os.Parcel
import android.text.TextUtils
import com.globalsion.annotations.AutoBundled
import com.globalsion.utils.ClassUtil
import java.lang.reflect.Field
import java.util.*

/**
 * Processor to process the annotation [AutoBundled]
 **/
object AutoBundledProcessor {
    /**
     * Scan the object that marked with [AutoBundled] will be save to the [Bundle].
     * @param bundle Bundle to save.
     * @param obj Object to scan.
     **/
    @Synchronized
    fun save(bundle: Bundle, obj: Any) {
        val values = LinkedHashMap<String, Any?>()
        ClassUtil.getFields(obj.javaClass).forEach {
            val annotation: AutoBundled = it.getAnnotation(AutoBundled::class.java) ?: return@forEach
            val key = if (TextUtils.isEmpty(annotation.name)) it.name else annotation.name

            if (values.containsKey(key)) {
                throw RuntimeException("AutoBundled: key '$key' already exists.")
            }

            it.isAccessible = true
            values[key] = it[obj]
        }

        if (bundle.containsKey("###AutoBundled###")) {
            throw Exception("Bundle already contains auto saved data.")
        }

        val parcel = Parcel.obtain()
        parcel.writeMap(values)
        val data = parcel.marshall()
        parcel.recycle()

        bundle.putByteArray("###AutoBundled###", data)
    }
    /**
     * Scan the object that marked with [AutoBundled] will be load from the [Bundle].
     * @param bundle Bundle to load.
     * @param obj Object to scan.
     **/
    @Synchronized
    fun load(bundle: Bundle, obj: Any) {
        val data = bundle.getByteArray("###AutoBundled###")
        val values = LinkedHashMap<String, Any?>()

        val parcel = Parcel.obtain()
        parcel.unmarshall(data, 0, data.size)
        parcel.setDataPosition(0)
        parcel.readMap(values, javaClass.classLoader)
        parcel.recycle()

        ClassUtil.getFields(obj.javaClass).forEach {
            val annotation: AutoBundled = it.getAnnotation(AutoBundled::class.java) ?: return@forEach
            val key = if (TextUtils.isEmpty(annotation.name)) it.name else annotation.name

            it.isAccessible = true
            it.set(obj, values[key])
        }
    }
}