package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.MasterEntity
import com.towamwms.enums.EnumBinGroup
import com.towamwms.enums.EnumBinType
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName

class Bin : MasterEntity {
    var type: EnumBinType = EnumBinType.BIN
    var group: EnumBinGroup = EnumBinGroup.STAGING
    @SerializedName("rack")
    var rack: Rack? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        rack = Rack()
        rack!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        type = EnumBinType.valueOf(parcel.readInt())
        group = EnumBinGroup.valueOf(parcel.readInt())
        rack = parcel.readObject()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeInt(type.value)
        parcel.writeInt(group.value)
        parcel.writeObject(rack, flags)
    }

    companion object CREATOR : Parcelable.Creator<Bin> {
        override fun createFromParcel(parcel: Parcel): Bin {
            return Bin(parcel)
        }

        override fun newArray(size: Int): Array<Bin?> {
            return arrayOfNulls(size)
        }
    }
}