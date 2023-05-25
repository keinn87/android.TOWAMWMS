package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.MasterEntity
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Location : MasterEntity {
    @SerializedName("warehouse")
    var warehouse: Warehouse? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        warehouse = Warehouse()
        warehouse!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        val p = Parcel.obtain()
        val data = parcel.readByteArray()!!
        p.unmarshall(data, 0, data.size)
        p.setDataPosition(0)
        warehouse = p.readParcelable(javaClass.classLoader)
        p.recycle()
        //warehouse = parcel.readObject()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        val p = Parcel.obtain()
        p.writeParcelable(warehouse, flags)
        parcel.writeByteArray(p.marshall())
        p.recycle()
        //parcel.writeObject(warehouse, flags)
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }
}