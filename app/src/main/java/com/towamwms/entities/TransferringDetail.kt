package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.Entity
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class TransferringDetail : Entity {
    @SerializedName("transferring")
    var transferring: Transferring? = null
    @SerializedName("part")
    var part: Part? = null
    @SerializedName("originBin")
    var originBin: Bin? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        transferring = Transferring()
        transferring!!.initialize()
        part = Part()
        part!!.initialize()
        originBin = Bin()
        originBin!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        part = parcel.readObject()
        originBin = parcel.readObject()

        if (flags == 0) {
            transferring = parcel.readObject()
        }
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeObject(part, flags)
        parcel.writeObject(originBin, flags)

        if (flags == 0) {
            parcel.writeObject(transferring, 1)
        }
    }

    companion object CREATOR : Parcelable.Creator<TransferringDetail> {
        override fun createFromParcel(parcel: Parcel): TransferringDetail {
            return TransferringDetail(parcel)
        }

        override fun newArray(size: Int): Array<TransferringDetail?> {
            return arrayOfNulls(size)
        }
    }
}