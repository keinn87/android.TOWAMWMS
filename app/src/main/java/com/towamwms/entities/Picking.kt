package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName
import com.towamwms.entities.bases.TransactionEntity

class Picking : TransactionEntity {
    @SerializedName("part")
    var part: Part? = null
    @SerializedName("originBin")
    var originBin: Bin? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        part = Part()
        part!!.initialize()
        originBin = Bin()
        originBin!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        part = parcel.readObject()
        originBin = parcel.readObject()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeObject(part, flags)
        parcel.writeObject(originBin, flags)
    }

    companion object CREATOR : Parcelable.Creator<Picking> {
        override fun createFromParcel(parcel: Parcel): Picking {
            return Picking(parcel)
        }

        override fun newArray(size: Int): Array<Picking?> {
            return arrayOfNulls(size)
        }
    }
}