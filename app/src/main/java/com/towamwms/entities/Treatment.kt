package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.MasterEntity

@Suppress("unused")
class Treatment : MasterEntity {

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    companion object CREATOR : Parcelable.Creator<Treatment> {
        override fun createFromParcel(parcel: Parcel): Treatment {
            return Treatment(parcel)
        }

        override fun newArray(size: Int): Array<Treatment?> {
            return arrayOfNulls(size)
        }
    }

}