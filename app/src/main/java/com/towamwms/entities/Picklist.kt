package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.globalsion.helpers.ParcelHelper
import com.towamwms.entities.bases.TransactionEntity

class Picklist : TransactionEntity {
    var mfgNo : String? = null
    var details: Set<PicklistDetail>? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()

        details = LinkedHashSet()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        mfgNo = parcel.readString()

        if (flags == 0) {
            details = parcel.readObjectList(PicklistDetail.CREATOR)?.toSet()
            details?.forEach { it.picklist = this }
        }
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeString(mfgNo)

        if (flags == 0) {
            parcel.writeObjectList(details?.toList(), 1)
        }
    }

    companion object CREATOR : Parcelable.Creator<Picklist> {
        override fun createFromParcel(parcel: Parcel): Picklist {
            return Picklist(parcel)
        }

        override fun newArray(size: Int): Array<Picklist?> {
            return arrayOfNulls(size)
        }
    }
}