package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.globalsion.helpers.ParcelHelper
import com.towamwms.entities.bases.Entity
import java.math.BigDecimal

@Suppress("unused")
class JobSummaryView : Entity {

    var mfgNo: String? = null
    var status: String? = null
    var totalMfgStatus: BigDecimal? = null
    var totalMfgNo: BigDecimal? = null
    var percentage: BigDecimal? = null


    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {

        mfgNo = parcel.readString()
        status = parcel.readString()
        totalMfgStatus = parcel.readBigDecimal()
        totalMfgNo = parcel.readBigDecimal()
        percentage = parcel.readBigDecimal()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {

        parcel.writeString(mfgNo)
        parcel.writeString(status)
        parcel.writeBigDecimal(totalMfgStatus)
        parcel.writeBigDecimal(totalMfgNo)
        parcel.writeBigDecimal(percentage)
    }

    companion object CREATOR : Parcelable.Creator<Part> {
        override fun createFromParcel(parcel: Parcel): Part {
            return Part(parcel)
        }

        override fun newArray(size: Int): Array<Part?> {
            return arrayOfNulls(size)
        }
    }


}