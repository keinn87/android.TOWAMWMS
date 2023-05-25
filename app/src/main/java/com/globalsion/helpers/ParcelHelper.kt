package com.globalsion.helpers

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.globalsion.annotations.AutoParceled
import com.globalsion.utils.ClassUtil
import java.io.Serializable
import java.lang.reflect.Modifier

import java.math.BigDecimal
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

@Suppress("unused", "MemberVisibilityCanBePrivate")
class ParcelHelper(var parcel: Parcel) {
    /*
    inline fun <reified T> read(): T {
        val clazz = T::class.java
        val isArray = clazz.isArray
        val isSet = Set::class.java.isAssignableFrom(clazz)
        val isList = List::class.java.isAssignableFrom(clazz)
        val typeParameters = clazz.typeParameters


        when {
            clazz == Char::class.javaPrimitiveType -> return readChar() as T
            clazz == Boolean::class.javaPrimitiveType -> return readBoolean() as T
            clazz == Byte::class.javaPrimitiveType -> return readByte() as T
            clazz == Short::class.javaPrimitiveType -> return readShort() as T
            clazz == Int::class.javaPrimitiveType -> return readInt() as T
            clazz == Long::class.javaPrimitiveType -> return readLong() as T
            clazz == Float::class.javaPrimitiveType -> return readFloat() as T
            clazz == Double::class.javaPrimitiveType -> return readDouble() as T

            clazz == Char::class.javaObjectType -> return readRefChar() as T
            clazz == Boolean::class.javaObjectType -> return readRefBoolean() as T
            clazz == Byte::class.javaObjectType -> return readRefByte() as T
            clazz == Short::class.javaObjectType -> return readRefShort() as T
            clazz == Int::class.javaObjectType -> return readRefInt() as T
            clazz == Long::class.javaObjectType -> return readRefLong() as T
            clazz == Float::class.javaObjectType -> return readRefFloat() as T
            clazz == Double::class.javaObjectType -> return readRefDouble() as T

            clazz == String::class.java -> return readString() as T
            clazz == UUID::class.java -> return readUUID() as T
            clazz == BigDecimal::class.java -> return readBigDecimal() as T
            clazz == Date::class.java -> return readDate() as T

            isArray && clazz.componentType == Char::class.javaPrimitiveType -> return readRefCharArray() as T
            isArray && clazz.componentType == Boolean::class.javaPrimitiveType -> return readRefBooleanArray() as T
            isArray && clazz.componentType == Byte::class.javaPrimitiveType -> return readRefByteArray() as T
            isArray && clazz.componentType == Short::class.javaPrimitiveType -> return readRefShortArray() as T
            isArray && clazz.componentType == Int::class.javaPrimitiveType -> return readRefIntArray() as T
            isArray && clazz.componentType == Long::class.javaPrimitiveType -> return readRefLongArray() as T
            isArray && clazz.componentType == Float::class.javaPrimitiveType -> return readRefFloatArray() as T
            isArray && clazz.componentType == Double::class.javaPrimitiveType -> return readRefDoubleArray() as T

            isArray && clazz.componentType == Char::class.javaObjectType -> return readRefCharArray() as T
            isArray && clazz.componentType == Boolean::class.javaObjectType -> return readRefBooleanArray() as T
            isArray && clazz.componentType == Byte::class.javaObjectType -> return readRefByteArray() as T
            isArray && clazz.componentType == Short::class.javaObjectType -> return readRefShortArray() as T
            isArray && clazz.componentType == Int::class.javaObjectType -> return readRefIntArray() as T
            isArray && clazz.componentType == Long::class.javaObjectType -> return readRefLongArray() as T
            isArray && clazz.componentType == Float::class.javaObjectType -> return readRefFloatArray() as T
            isArray && clazz.componentType == Double::class.javaObjectType -> return readRefDoubleArray() as T

            isArray && clazz.componentType == String::class.java -> return readStringArray() as T
            isArray && clazz.componentType == UUID::class.java -> return readUUIDArray() as T
            isArray && clazz.componentType == BigDecimal::class.java -> return readBigDecimalArray() as T
            isArray && clazz.componentType == Date::class.java -> return readDateArray() as T

            isSet && typeParameters[0] == Char::class.javaObjectType -> {
                val values = readRefCharArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == Boolean::class.javaObjectType -> {
                val values = readRefBooleanArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == Byte::class.javaObjectType -> {
                val values = readRefByteArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == Short::class.javaObjectType -> {
                val values = readRefShortArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == Int::class.javaObjectType -> {
                val values = readRefIntArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == Long::class.javaObjectType -> {
                val values = readRefLongArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == Float::class.javaObjectType -> {
                val values = readRefFloatArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == Double::class.javaObjectType -> {
                val values = readRefDoubleArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }

            isSet && typeParameters[0] == UUID::class.java -> {
                val values = readUUIDArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == String::class.java -> {
                val values = readStringArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == BigDecimal::class.java -> {
                val values = readBigDecimalArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }
            isSet && typeParameters[0] == Date::class.java -> {
                val values = readDateArray()
                return (if (values != null) LinkedHashSet(values.asList()) else null) as T
            }

            isList && typeParameters[0] == Char::class.javaObjectType -> {
                val values = readRefCharArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == Boolean::class.javaObjectType -> {
                val values = readRefBooleanArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == Byte::class.javaObjectType -> {
                val values = readRefByteArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == Short::class.javaObjectType -> {
                val values = readRefShortArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == Int::class.javaObjectType -> {
                val values = readRefIntArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == Long::class.javaObjectType -> {
                val values = readRefLongArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == Float::class.javaObjectType -> {
                val values = readRefFloatArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == Double::class.javaObjectType -> {
                val values = readRefDoubleArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }

            isList && typeParameters[0] == UUID::class.java -> {
                val values = readUUIDArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == String::class.java -> {
                val values = readStringArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == BigDecimal::class.java -> {
                val values = readBigDecimalArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }
            isList && typeParameters[0] == Date::class.java -> {
                val values = readDateArray()
                return (if (values != null) ArrayList(values.asList()) else null) as T
            }

            else -> throw UnsupportedOperationException("Type not supported")
        }
    }
    */

    fun writeChar(c: Char) {
        parcel.writeValue(c)
    }

    fun writeBoolean(b: Boolean) {
        parcel.writeByte((if (b) 1 else 0).toByte())
    }

    fun writeByte(b: Byte) {
        parcel.writeByte(b)
    }

    fun writeShort(s: Short) {
        parcel.writeValue(s)
    }

    fun writeInt(i: Int) {
        parcel.writeInt(i)
    }

    fun writeLong(l: Long) {
        parcel.writeLong(l)
    }

    fun writeFloat(f: Float) {
        parcel.writeFloat(f)
    }

    fun writeDouble(d: Double) {
        parcel.writeDouble(d)
    }

    fun writeString(value: String?) {
        parcel.writeString(value)
    }

    fun writeObject(`object`: Parcelable?, parcelableFlags: Int) {
        parcel.writeParcelable(`object`, parcelableFlags)
    }

    fun writeBigDecimal(d: BigDecimal?) {
        parcel.writeValue(d)
    }

    fun writeUUID(u: UUID?) {
        parcel.writeValue(u)
    }

    fun writeDate(d: Date?) {
        parcel.writeValue(d)
    }

    fun writeCharArray(c: CharArray?) {
        parcel.writeCharArray(c)
    }

    fun writeBooleanArray(b: BooleanArray?) {
        parcel.writeBooleanArray(b)
    }

    fun writeByteArray(b: ByteArray?) {
        parcel.writeByteArray(b)
    }

    fun writeByteArray(b: ByteArray?, offset: Int, length: Int) {
        parcel.writeByteArray(b, offset, length)
    }

    fun writeShortArray(s: ShortArray?) {
        if (s == null) {
            parcel.writeInt(-1)
        } else {
            val byteBuffer = ByteBuffer.allocate(s.size * 2)
            for (value in s) {
                byteBuffer.putShort(value)
            }

            parcel.writeInt(s.size)
            parcel.writeByteArray(byteBuffer.array())
        }
    }

    fun writeIntArray(i: IntArray?) {
        parcel.writeIntArray(i)
    }

    fun writeLongArray(l: LongArray?) {
        parcel.writeLongArray(l)
    }

    fun writeFloatArray(f: FloatArray?) {
        parcel.writeFloatArray(f)
    }

    fun writeDoubleArray(d: DoubleArray?) {
        parcel.writeDoubleArray(d)
    }

    fun writeStringArray(s: Array<String?>?) {
        parcel.writeStringArray(s)
    }

    fun writeStringList(s: List<String?>?) {
        parcel.writeStringList(s)
    }

    fun <T : Parcelable> writeObjectArray(objects: Array<T>?, parcelableFlags: Int) {
        parcel.writeTypedArray(objects, parcelableFlags)
    }

    inline fun <reified T : Parcelable> writeObjectList(objects: Collection<T>?, parcelableFlags: Int) {
        if (objects != null) {
            val array = objects.toTypedArray()
            writeObjectArray(array, parcelableFlags)
        } else {
            writeObjectArray<Parcelable>(null, parcelableFlags)
        }
    }

    fun writeBigDecimalArray(d: Array<BigDecimal?>?) {
        parcel.writeValue(d)
    }

    fun writeUUIDArray(u: Array<UUID?>?) {
        parcel.writeValue(u)
    }

    fun writeDateArray(d: Array<Date?>?) {
        parcel.writeValue(d)
    }

    fun writeRefChar(c: Char?) {
        parcel.writeValue(c)
    }

    fun writeRefBoolean(b: Boolean?) {
        parcel.writeValue(b)
    }

    fun writeRefByte(b: Byte?) {
        parcel.writeValue(b)
    }

    fun writeRefShort(s: Short?) {
        parcel.writeValue(s)
    }

    fun writeRefInt(i: Int?) {
        parcel.writeValue(i)
    }

    fun writeRefLong(l: Long?) {
        parcel.writeValue(l)
    }

    fun writeRefFloat(f: Float?) {
        parcel.writeValue(f)
    }

    fun writeRefDouble(d: Double?) {
        parcel.writeValue(d)
    }

    fun writeRefCharArray(c: Array<Char?>?) {
        parcel.writeValue(c)
    }

    fun writeRefBooleanArray(b: Array<Boolean?>?) {
        parcel.writeValue(b)
    }

    fun writeRefByteArray(b: Array<Byte?>?) {
        parcel.writeValue(b)
    }

    fun writeRefByteArray(b: Array<Byte>?, offset: Int, length: Int) {
        parcel.writeValue(b)
        if (b == null) {
            parcel.writeValue(null)
        } else {
            val tmp = arrayOfNulls<Byte>(length)
            var ti = 0
            var bi = offset
            val c = offset + length
            while (bi < c) {
                tmp[ti] = b[bi]
                ti++
                bi++
            }

            parcel.writeValue(tmp)
        }
    }

    fun writeRefIntArray(i: Array<Int?>?) {
        parcel.writeValue(i)
    }

    fun writeRefShortArray(s: Array<Short?>?) {
        parcel.writeValue(s)
    }

    fun writeRefLongArray(l: Array<Long?>?) {
        parcel.writeValue(l)
    }

    fun writeRefFloatArray(f: Array<Float?>?) {
        parcel.writeValue(f)
    }

    fun writeRefDoubleArray(d: Array<Double?>?) {
        parcel.writeValue(d)
    }

    fun writeValue(o: Any?) {
        parcel.writeValue(o)
    }

    @SuppressLint("ParcelClassLoader")
    fun readChar(): Char {
        return parcel.readValue(javaClass.classLoader) as Char
    }

    fun readBoolean(): Boolean {
        return parcel.readByte().toInt() == 1
    }

    fun readByte(): Byte {
        return parcel.readByte()
    }

    @SuppressLint("ParcelClassLoader")
    fun readShort(): Short {
        return parcel.readValue(javaClass.classLoader) as Short
    }

    fun readInt(): Int {
        return parcel.readInt()
    }

    fun readLong(): Long {
        return parcel.readLong()
    }

    fun readFloat(): Float {
        return parcel.readFloat()
    }

    fun readDouble(): Double {
        return parcel.readDouble()
    }

    fun readString(): String? {
        return parcel.readString()
    }

    fun readObject(loader: ClassLoader): Any? {
        return parcel.readValue(loader)
    }

    inline fun <reified T : Parcelable> readObject(): T? {
        return parcel.readParcelable(T::class.java.classLoader)
    }

    fun <T : Parcelable> readObject(clazz: Class<T>): T? {
        return parcel.readParcelable(clazz.classLoader)
    }

    @SuppressLint("ParcelClassLoader")
    fun readValue(): Any? {
        return parcel.readValue(javaClass.classLoader)
    }

    @SuppressLint("ParcelClassLoader")
    fun readBigDecimal(): BigDecimal? {
        return parcel.readValue(javaClass.classLoader) as BigDecimal?
    }

    @SuppressLint("ParcelClassLoader")
    fun readUUID(): UUID? {
        return parcel.readValue(javaClass.classLoader) as UUID?
    }

    @SuppressLint("ParcelClassLoader")
    fun readDate(): Date? {
        return parcel.readValue(javaClass.classLoader) as Date?
    }

    fun readCharArray(): CharArray? {
        return parcel.createCharArray()
    }

    fun readBooleanArray(): BooleanArray? {
        return parcel.createBooleanArray()
    }

    fun readByteArray(): ByteArray? {
        return parcel.createByteArray()
    }

    fun readShortArray(): ShortArray? {
        val length = parcel.readInt()
        if (length == 0) return ShortArray(0)

        val s = ShortArray(length)
        val buffer = ByteArray(s.size * 2)

        parcel.readByteArray(buffer)
        val byteBuffer = ByteBuffer.wrap(buffer)
        var i = 0
        val c = s.size
        while (i < c) {
            s[i] = byteBuffer.short
            i++
        }
        return s
    }

    fun readIntArray(): IntArray? {
        return parcel.createIntArray()
    }

    fun readLongArray(): LongArray? {
        return parcel.createLongArray()
    }

    fun readFloatArray(): FloatArray? {
        return parcel.createFloatArray()
    }

    fun readDoubleArray(): DoubleArray? {
        return parcel.createDoubleArray()
    }

    fun readStringArray(): Array<String?>? {
        return parcel.createStringArray()
    }

    fun readStringList(): List<String?>? {
        val s = ArrayList<String?>()
        parcel.readStringList(s)
        return s
    }

    fun <T : Parcelable> readObjectArray(creator: Parcelable.Creator<T>): Array<T>? {
        return parcel.createTypedArray(creator)
    }

    fun <T : Parcelable> readObjectList(creator: Parcelable.Creator<T>): List<T>? {
        val array = readObjectArray(creator) ?: return null

        return ArrayList(Arrays.asList(*array))
    }

    fun <T : Parcelable, L : MutableCollection<T>> readObjectList(creator: Parcelable.Creator<T>, collection: L): L? {
        val array = readObjectArray(creator) ?: return null

        collection.addAll(array.asList())
        return collection
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readBigDecimalArray(): Array<BigDecimal?>? {
        return parcel.readValue(javaClass.classLoader) as Array<BigDecimal?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readUUIDArray(): Array<UUID?>? {
        return parcel.readValue(javaClass.classLoader) as Array<UUID?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readDateArray(): Array<Date?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Date?>? ?: return null
    }

    @SuppressLint("ParcelClassLoader")
    fun readRefChar(): Char? {
        return parcel.readValue(javaClass.classLoader) as Char?
    }

    @SuppressLint("ParcelClassLoader")
    fun readRefBoolean(): Boolean? {
        return parcel.readValue(javaClass.classLoader) as Boolean?
    }

    @SuppressLint("ParcelClassLoader")
    fun readRefByte(): Byte? {
        return parcel.readValue(javaClass.classLoader) as Byte?
    }

    @SuppressLint("ParcelClassLoader")
    fun readRefShort(): Short? {
        return parcel.readValue(javaClass.classLoader) as Short?
    }

    @SuppressLint("ParcelClassLoader")
    fun readRefInt(): Int? {
        return parcel.readValue(javaClass.classLoader) as Int?
    }

    @SuppressLint("ParcelClassLoader")
    fun readRefLong(): Long? {
        return parcel.readValue(javaClass.classLoader) as Long?
    }

    @SuppressLint("ParcelClassLoader")
    fun readRefFloat(): Float? {
        return parcel.readValue(javaClass.classLoader) as Float?
    }

    @SuppressLint("ParcelClassLoader")
    fun readRefDouble(): Double? {
        return parcel.readValue(javaClass.classLoader) as Double?
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readRefCharArray(): Array<Char?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Char?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readRefBooleanArray(): Array<Boolean?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Boolean?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readRefByteArray(): Array<Byte?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Byte?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readRefShortArray(): Array<Short?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Short?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readRefIntArray(): Array<Int?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Int?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readRefLongArray(): Array<Long?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Long?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readRefFloatArray(): Array<Float?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Float?>? ?: return null
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ParcelClassLoader")
    fun readRefDoubleArray(): Array<Double?>? {
        return parcel.readValue(javaClass.classLoader) as Array<Double?>? ?: return null
    }

    fun readValue(loader: ClassLoader): Any? {
        return parcel.readValue(loader)
    }

    fun dataPosition(): Int {
        return parcel.dataPosition()
    }

    fun setDataPosition(position: Int) {
        parcel.setDataPosition(position)
    }

    fun dataCapacity(): Int {
        return parcel.dataCapacity()
    }

    fun setDataCapacity(size: Int) {
        parcel.setDataCapacity(size)
    }

    fun dataSize(): Int {
        return parcel.dataSize()
    }

    fun setDataSize(size: Int) {
        parcel.setDataSize(size)
    }

    @JvmOverloads
    fun unmarshall(b: ByteArray, offset: Int = 0, length: Int = b.size) {
        parcel.unmarshall(b, offset, length)
    }

    fun marshall(): ByteArray {
        return parcel.marshall()
    }

    companion object {
        private val knownTypes = arrayOf(
                Char::class.javaObjectType,
                Boolean::class.javaObjectType,
                Byte::class.javaObjectType,
                Short::class.javaObjectType,
                Int::class.javaObjectType,
                Long::class.javaObjectType,
                Float::class.javaObjectType,
                Double::class.javaObjectType,
                String::class.java,
                BigDecimal::class.java,
                Date::class.java)

        /**
         * Experimental!! Use at your own risk.
         * Save fields in the object to parcel.
         * @param parcel Parcel to save
         * @param flags Parcel flags
         * @param obj Object to save
         * @param callback Perform callback when saving parcelable types.
         * @see AutoParceled
         **/
        fun save(parcel: Parcel, flags: Int, obj: Parcelable, callback: SaveFieldCallback? = null) {
            val objectFields = ClassUtil.getFields(Any::class.java)
            val fields = ClassUtil.getFields(obj.javaClass)
                    .filter { x -> !objectFields.any { y -> x == y} }
                    .filter { !Modifier.isStatic(it.modifiers) }
                    .sortedBy { it.name }

            parcel.writeInt(flags)

            fields.forEach { field ->
                field.isAccessible = true
                val value = field.get(obj)

                if (value is Parcelable || value is Serializable) {
                    val annotation: AutoParceled? = field.getAnnotation(AutoParceled::class.java)

                    if (annotation != null ||
                            knownTypes.any { it == value.javaClass } ||
                            value.javaClass.isPrimitive) {
                        parcel.writeValue(value)
                    } else {
                        if (callback == null) {
                            throw IllegalArgumentException("callback is required for this object.");
                        } else {
                            callback.onSaveField(parcel, flags, field.name, value)
                        }
                    }
                } else {
                    parcel.writeValue(value)
                }
            }
        }
        /**
         * Experimental!! Use at your own risk.
         * Load fields in the object from parcel.
         * @param parcel Parcel to load
         * @param obj Object to load
         * @param callback Perform callback when saving parcelable types.
         * @see AutoParceled
         **/
        fun load(parcel: Parcel, obj: Parcelable, callback: LoadFieldCallback? = null) {
            val objectFields = ClassUtil.getFields(Any::class.java)
            val fields = ClassUtil.getFields(obj.javaClass)
                    .filter { x -> !objectFields.any { y -> x == y} }
                    .filter { !Modifier.isStatic(it.modifiers) }
                    .sortedBy { it.name }

            val flags = parcel.readInt()

            fields.forEach { field ->
                field.isAccessible = true

                if (Parcelable::class.java.isAssignableFrom(field.type) ||
                        Serializable::class.java.isAssignableFrom(field.type)) {
                    val annotation: AutoParceled? = field.getAnnotation(AutoParceled::class.java)

                    if (annotation != null ||
                            knownTypes.any { it == field.type } ||
                            field.type.isPrimitive) {
                        field.set(obj, parcel.readValue(ParcelHelper::class.java.classLoader))
                    } else {
                        if (callback == null) {
                            throw IllegalArgumentException("callback is required for this object.");
                        } else {
                            field.set(obj, callback.onLoadField(parcel, flags, field.name))
                        }
                    }
                } else {
                    field.set(obj, parcel.readValue(ParcelHelper::class.java.classLoader))
                }
            }
        }
    }

    interface SaveFieldCallback {
        fun onSaveField(parcel: Parcel, flags: Int, fieldName: String, value: Any?)
    }

    interface LoadFieldCallback {
        fun onLoadField(parcel: Parcel, flags: Int, fieldName: String) : Any?
    }
}
