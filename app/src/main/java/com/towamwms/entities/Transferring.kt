package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.TransactionEntity
import com.towamwms.enums.EnumTransferringType
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName

class Transferring : TransactionEntity {
    var type: EnumTransferringType = EnumTransferringType.SINGLE
    @SerializedName("destinationBin")
    var destinationBin: Bin? = null
    @SerializedName("details")
    var details: Set<TransferringDetail>? = null

    @SerializedName("sourcePart")
    var sourcePart: Part? = null
    @SerializedName("sourceBin")
    var sourceBin: Bin? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        destinationBin = Bin()
        destinationBin!!.initialize()
        details = LinkedHashSet()

        sourcePart = Part()
        sourcePart!!.initialize()
        sourceBin = Bin()
        sourceBin!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        type = EnumTransferringType.valueOf(parcel.readInt())
        destinationBin = parcel.readObject()
        sourcePart = parcel.readObject()
        sourceBin = parcel.readObject()

        if (flags == 0) {
            details = parcel.readObjectList(TransferringDetail)?.toSet()
            details?.forEach { it.transferring = this }
        }
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeInt(type.value)
        parcel.writeObject(destinationBin, flags)
        parcel.writeObject(sourcePart, flags)
        parcel.writeObject(sourceBin, flags)

        if (flags == 0) {
            parcel.writeObjectList(details?.toList(), 1)
        }
    }

    companion object CREATOR : Parcelable.Creator<Transferring> {
        override fun createFromParcel(parcel: Parcel): Transferring {
            return Transferring(parcel)
        }

        override fun newArray(size: Int): Array<Transferring?> {
            return arrayOfNulls(size)
        }
    }
}