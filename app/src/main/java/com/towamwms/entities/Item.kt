package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.MasterEntity
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Item : MasterEntity {
    var requiredTreatment: Boolean = false
    @SerializedName("itemGroup")
    var itemGroup: ItemGroup? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        itemGroup = ItemGroup()
        itemGroup!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        requiredTreatment = parcel.readBoolean()
        itemGroup = parcel.readObject()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeBoolean(requiredTreatment)
        parcel.writeObject(itemGroup, flags)
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}