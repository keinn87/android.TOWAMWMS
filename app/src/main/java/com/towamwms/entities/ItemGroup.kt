package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.MasterEntity

@Suppress("unused")
class ItemGroup : MasterEntity {

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    companion object CREATOR : Parcelable.Creator<ItemGroup> {
        override fun createFromParcel(parcel: Parcel): ItemGroup {
            return ItemGroup(parcel)
        }

        override fun newArray(size: Int): Array<ItemGroup?> {
            return arrayOfNulls(size)
        }
    }
}