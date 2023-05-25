package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName
import com.towamwms.entities.bases.TransactionEntity

class Issuing : TransactionEntity {
    var mfgNo : String? = null
    @SerializedName("userSigned")
    var userSigned : User? = null
    @SerializedName("picklistDetails")
    var picklistDetails : Set<PicklistDetail>? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()

        userSigned = User()
        userSigned!!.initialize()
        picklistDetails = LinkedHashSet()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        mfgNo = parcel.readString()
        userSigned = parcel.readObject()

        if (flags == 0) {
            picklistDetails = parcel.readObjectList(PicklistDetail.CREATOR)?.toSet()
            picklistDetails?.forEach { it.issuing = this }
        }
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeString(mfgNo)
        parcel.writeObject(userSigned, flags)

        if (flags == 0) {
            parcel.writeObjectList(picklistDetails?.toList(), 1)
        }
    }

    companion object CREATOR : Parcelable.Creator<Issuing> {
        override fun createFromParcel(parcel: Parcel): Issuing {
            return Issuing(parcel)
        }

        override fun newArray(size: Int): Array<Issuing?> {
            return arrayOfNulls(size)
        }
    }
}