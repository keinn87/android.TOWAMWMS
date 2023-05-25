package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.Entity
import com.globalsion.helpers.ParcelHelper

@Suppress("unused")
open class User : Entity {
    var username: String? = null
    var password: String? = null
    var name: String? = null
    var email: String? = null

    //protected var userRoles: Set<UserRole?>? = null

    constructor()
    constructor(parcel: Parcel): super(parcel)

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)
        username = parcel.readString()
        password = parcel.readString()
        name = parcel.readString()
        email = parcel.readString()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeString(name)
        parcel.writeString(email)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}