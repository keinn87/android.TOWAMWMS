package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.Entity
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName
import com.towamwms.enums.EnumPartStatus
import com.towamwms.enums.EnumPartType

@Suppress("unused")
class Part: Entity {
    var barcode: String? = null
    var mfgNo: String? = null
    var lineNo: String? = null
    var rCode: String? = null
    var drawingNo: String? = null
    var processCD1: String? = null
    var processCD2: String? = null
    var processCD3: String? = null
    var quality: String? = null
    var tracking: String? = null
    var quantity: Int = 0
    var type: EnumPartType = EnumPartType.NONPO
    var status: EnumPartStatus = EnumPartStatus.OPEN
    var revision: Int = 0
    var treated: Boolean = false
    @SerializedName("item")
    var item: Item? = null
    @SerializedName("treatment")
    var treatment: Treatment? = null
    @SerializedName("bin")
    var bin: Bin? = null
    @SerializedName("partPurchase")
    var partPurchase: PartPurchase? = null


    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
        item = Item()
        item!!.initialize()
        treatment = Treatment()
        treatment!!.initialize()
        bin = Bin()
        bin!!.initialize()
        partPurchase = PartPurchase()
        partPurchase!!.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        barcode = parcel.readString()
        mfgNo = parcel.readString()
        lineNo = parcel.readString()
        rCode = parcel.readString()
        drawingNo = parcel.readString()
        processCD1 = parcel.readString()
        processCD2 = parcel.readString()
        processCD3 = parcel.readString()
        quality = parcel.readString()
        tracking = parcel.readString()
        quantity = parcel.readInt()
        type = EnumPartType.valueOf(parcel.readInt())
        status = EnumPartStatus.valueOf(parcel.readInt())
        revision = parcel.readInt()
        treated = parcel.readBoolean()

        item = parcel.readObject()
        treatment = parcel.readObject()
        bin = parcel.readObject()
        partPurchase = parcel.readObject()
    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)

        parcel.writeString(barcode)
        parcel.writeString(mfgNo)
        parcel.writeString(lineNo)
        parcel.writeString(rCode)
        parcel.writeString(drawingNo)
        parcel.writeString(processCD1)
        parcel.writeString(processCD2)
        parcel.writeString(processCD3)
        parcel.writeString(quality)
        parcel.writeString(tracking)
        parcel.writeInt(quantity)
        parcel.writeInt(type.value)
        parcel.writeInt(status.value)
        parcel.writeInt(revision)
        parcel.writeBoolean(treated)

        parcel.writeObject(item, flags)
        parcel.writeObject(treatment, flags)
        parcel.writeObject(bin, flags)
        parcel.writeObject(partPurchase,flags)
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