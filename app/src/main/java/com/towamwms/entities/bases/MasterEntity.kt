package com.towamwms.entities.bases

import android.os.Parcel
import com.globalsion.helpers.ParcelHelper

abstract class MasterEntity : Entity {
    var code: String? = null
    var name: String? = null
    var description: String? = null
    var active: Boolean = true

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        code = parcel.readString()
        name = parcel.readString()
        description = parcel.readString()
        active = parcel.readBoolean()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeString(code)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeBoolean(active)
    }
}