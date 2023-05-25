package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.MasterEntity
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Rack : MasterEntity {
    @SerializedName("location")
    var location: Location? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        location = Location()
        location!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        location = parcel.readObject()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeObject(location, flags)
    }

    companion object CREATOR : Parcelable.Creator<Rack> {
        override fun createFromParcel(parcel: Parcel): Rack {
            return Rack(parcel)
        }

        override fun newArray(size: Int): Array<Rack?> {
            return arrayOfNulls(size)
        }
    }
}