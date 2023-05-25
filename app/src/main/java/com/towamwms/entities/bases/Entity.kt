package com.towamwms.entities.bases

import android.os.Parcel
import android.os.Parcelable
import com.globalsion.helpers.ParcelHelper
import java.util.*

@Suppress("unused", "EqualsOrHashCode", "LeakingThis")
abstract class Entity() : Parcelable {
    var id: Long = 0
    var version: Int = 0
    var deleted: Boolean = false
    var dateDeleted: Date? = null
    var dateCreated: Date? = null
    var userCreated: Long? = null
    var dateModified: Date? = null
    var userModified: Long? = null

    constructor(parcel: Parcel) : this() {
        val flags = parcel.readInt()
        readFromParcel(ParcelHelper(parcel), flags)
    }

    open fun initialize() {
        dateCreated = Date()
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> return false
            other == this -> return true
            other.javaClass != this.javaClass -> return false
            (other as Entity).id == this.id -> return true
            else -> false
        }
    }

    protected open fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        id = parcel.readLong()
        version = parcel.readInt()
        deleted = parcel.readBoolean()
        dateDeleted = parcel.readDate()
        dateCreated = parcel.readDate()
        userCreated = parcel.readRefLong()
        dateModified = parcel.readDate()
        userModified = parcel.readRefLong()
    }

    protected open fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(version)
        parcel.writeBoolean(deleted)
        parcel.writeDate(dateDeleted)
        parcel.writeDate(dateCreated)
        parcel.writeRefLong(userCreated)
        parcel.writeDate(dateModified)
        parcel.writeRefLong(userModified)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(flags)
        writeToParcel(ParcelHelper(parcel), flags)
    }

    override fun describeContents(): Int = 0
}