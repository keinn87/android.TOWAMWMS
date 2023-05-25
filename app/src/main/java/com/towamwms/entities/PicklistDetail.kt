package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName
import com.towamwms.entities.bases.Entity

class PicklistDetail : Entity {
    @SerializedName("picklist")
    var picklist: Picklist? = null
    @SerializedName("part")
    var part: Part? = null
    @SerializedName("issuing")
    var issuing: Issuing? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        picklist = Picklist()
        picklist!!.initialize()
        part = Part()
        part!!.initialize()
        issuing = Issuing()
        issuing!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        part = parcel.readObject()

        if (flags == 0) {
            picklist = parcel.readObject()
            issuing = parcel.readObject()
        }
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeObject(part, flags)

        if (flags == 0) {
            parcel.writeObject(picklist, 1)
            parcel.writeObject(issuing, 1)
        }
    }

    companion object CREATOR : Parcelable.Creator<PicklistDetail> {
        override fun createFromParcel(parcel: Parcel): PicklistDetail {
            return PicklistDetail(parcel)
        }

        override fun newArray(size: Int): Array<PicklistDetail?> {
            return arrayOfNulls(size)
        }
    }
}