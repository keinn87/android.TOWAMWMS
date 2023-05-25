package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.TransactionEntity
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Receiving : TransactionEntity {
    @SerializedName("part")
    var part: Part? = null
    @SerializedName("bin")
    var bin: Bin? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        part = Part()
        part!!.initialize()
        bin = Bin()
        bin!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        part = parcel.readObject()
        bin = parcel.readObject()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeObject(part, flags)
        parcel.writeObject(bin, flags)
    }

    companion object CREATOR : Parcelable.Creator<Receiving> {
        override fun createFromParcel(parcel: Parcel): Receiving {
            return Receiving(parcel)
        }

        override fun newArray(size: Int): Array<Receiving?> {
            return arrayOfNulls(size)
        }
    }
}