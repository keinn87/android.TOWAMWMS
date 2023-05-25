package com.towamwms.entities.bases

import android.os.Parcel
import com.globalsion.helpers.ParcelHelper
import com.globalsion.utils.DateUtil
import java.util.*

@Suppress("unused")
abstract class TransactionEntity : Entity {
    var documentNo: String? = null
    var documentDate: Date? = null

    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        documentDate = DateUtil.today()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)
        documentNo = parcel.readString()
        documentDate = parcel.readDate()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(documentNo)
        parcel.writeDate(documentDate)
    }
}