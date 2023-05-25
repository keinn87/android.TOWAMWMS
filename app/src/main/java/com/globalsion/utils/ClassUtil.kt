package com.globalsion.utils

import java.lang.ref.SoftReference
import java.lang.reflect.Field
import java.util.LinkedHashSet

object ClassUtil {
    private var softClazzes: SoftReference<HashMap<String, Array<Any?>>>? = null
    private var softFields: SoftReference<HashMap<Class<*>, Array<Field>>>? = null

    @Synchronized
    @Throws(ClassNotFoundException::class)
    @Suppress("LiftReturnOrAssignment")
    fun forName(className: String) : Class<*> {
        var clazzes = softClazzes?.get()

        if (clazzes == null) {
            clazzes = HashMap()
            ClassUtil.softClazzes = SoftReference(clazzes)
        }

        var value = clazzes[className]

        if (value != null) {
            if (value[0] as Boolean) {
                return value[1] as Class<*>
            } else {
                throw value[2] as ClassNotFoundException
            }
        } else {
            try {
                value = arrayOf(true, Class.forName(className), null)
                clazzes[className] = value
                return value[1] as Class<*>
            } catch (e: ClassNotFoundException) {
                value = arrayOf(false, null, e)
                clazzes[className] = value
                throw e
            }
        }
    }

    @Synchronized
    fun getFields(clazz: Class<*>): Array<Field> {
        var cached = softFields?.get()

        if (cached == null) {
            cached = HashMap()
            softFields = SoftReference(cached)
        }

        if (cached.containsKey(clazz)) {
            return cached[clazz]!!
        } else {
            val clazzes = LinkedHashSet<Class<*>>()
            val fields = LinkedHashSet<Field>()
            clazzes.add(clazz)

            var tmpClazz = clazz
            while (tmpClazz.superclass != null) {
                tmpClazz = tmpClazz.superclass
                clazzes.add(tmpClazz)
            }

            clazzes.reversed().forEach {
                fields.addAll(it.declaredFields)
            }

            return { cached[clazz] = fields.toTypedArray(); cached[clazz] }()!!
        }
    }
}