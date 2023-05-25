package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.MasterEntity

@Suppress("unused")
class Warehouse : MasterEntity {

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    companion object CREATOR : Parcelable.Creator<Warehouse> {
        override fun createFromParcel(parcel: Parcel): Warehouse {
            return Warehouse(parcel)
        }

        override fun newArray(size: Int): Array<Warehouse?> {
            return arrayOfNulls(size)
        }
    }
}