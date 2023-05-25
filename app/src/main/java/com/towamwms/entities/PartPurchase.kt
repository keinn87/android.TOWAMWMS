package com.towamwms.entities

import android.os.Parcel
import android.os.Parcelable
import com.towamwms.entities.bases.Entity
import com.globalsion.helpers.ParcelHelper
import com.google.gson.annotations.SerializedName
import com.towamwms.enums.EnumPartStatus
import com.towamwms.enums.EnumPartType
import java.math.BigDecimal
import java.util.*

@Suppress("unused")
class PartPurchase : Entity {
    var vendorCode: String? = null
    var vendorDesc: String? = null
    var vendorSubrange: String? = null
    var vendorSubrangeDesc: String? = null

    // PO
    var poNo: String? = null
    var poLine: String? = null
    var poDate: Date? = null
    var poQty: Int = 0
    var scheduledQty: Int = 0
    var grQty: Int = 0
    var poUom: String? = null
    var poPrice: BigDecimal? = null
    var poPriceUnit: String? = null
    var poAmount: BigDecimal? = null
    var currency: String? = null

    // PR
    var prNo: String? = null
    var prLine: String? = null
    var tracking: String? = null

    // Material Document
    var materialDocNo: String? = null
    var materialDocYear: String? = null
    var materialDocLine: String? = null
    var materialDocEntryDate: Date? = null
    var materialDocPostingDate: Date? = null
    var materialDocPrice: BigDecimal? = null
    var materialDocQty: Int = 0
    var materialDocQtyUnit: String? = null
    var materialDocAmount: BigDecimal? = null
    var materialDocCurrency: String? = null

    // Misc
    var invoiceNo: String? = null
    var acctAsgmtCategory: String? = null
    var contactPerson: String? = null
    var purchasingOrg: String? = null
    var purchasingGroup: String? = null
    var materialCode: String? = null
    var materialDesc: String? = null
    var mfrPartNo: String? = null
    var manufacturer: String? = null
    var manufacturerDesc: String? = null
    var plant: String? = null
    var storageLocation: String? = null
    var materialGroupDesc: String? = null
    var glAcct: String? = null
    var glAcctDesc: String? = null
    var wbsElement: String? = null
    var costCenter: String? = null
    var deliveryDate: Date? = null
    var taxCode: String? = null
    var ers: String? = null


    constructor()
    constructor(parcel: Parcel) : super(parcel)

    override fun initialize() {
        super.initialize()
    }

    override fun readFromParcel(parcel: ParcelHelper, flags: Int) {
        super.readFromParcel(parcel, flags)

        vendorCode = parcel.readString()
        vendorDesc = parcel.readString()
        vendorSubrange = parcel.readString()
        vendorSubrangeDesc = parcel.readString()

        poNo = parcel.readString()
        poLine = parcel.readString()
        poDate = parcel.readDate()
        poQty = parcel.readInt()
        scheduledQty = parcel.readInt()
        grQty = parcel.readInt()
        poUom = parcel.readString()
        poPrice = parcel.readBigDecimal()
        poPriceUnit = parcel.readString()
        poAmount = parcel.readBigDecimal()
        currency = parcel.readString()

        prNo = parcel.readString()
        prLine = parcel.readString()
        tracking = parcel.readString()

        materialDocNo = parcel.readString()
        materialDocYear = parcel.readString()
        materialDocLine = parcel.readString()
        materialDocEntryDate = parcel.readDate()
        materialDocPostingDate = parcel.readDate()
        materialDocPrice = parcel.readBigDecimal()
        materialDocQty = parcel.readInt()
        materialDocQtyUnit = parcel.readString()
        materialDocAmount = parcel.readBigDecimal()
        materialDocCurrency = parcel.readString()

        invoiceNo = parcel.readString()
        acctAsgmtCategory = parcel.readString()
        contactPerson = parcel.readString()
        purchasingOrg = parcel.readString()
        purchasingGroup = parcel.readString()
        materialCode = parcel.readString()
        materialDesc = parcel.readString()
        mfrPartNo = parcel.readString()
        manufacturer = parcel.readString()
        manufacturerDesc = parcel.readString()
        plant = parcel.readString()
        storageLocation = parcel.readString()
        materialGroupDesc = parcel.readString()
        glAcct = parcel.readString()
        glAcctDesc = parcel.readString()
        wbsElement = parcel.readString()
        costCenter = parcel.readString()
        deliveryDate = parcel.readDate()
        taxCode = parcel.readString()
        ers = parcel.readString()


    }

    override fun writeToParcel(parcel: ParcelHelper, flags: Int) {
        super.writeToParcel(parcel, flags)


        parcel.writeString(vendorCode)
        parcel.writeString(vendorDesc)
        parcel.writeString(vendorSubrange)
        parcel.writeString(vendorSubrangeDesc)

        parcel.writeString(poNo)
        parcel.writeString(poLine)
        parcel.writeDate(poDate)
        parcel.writeInt(poQty)
        parcel.writeInt(scheduledQty)
        parcel.writeInt(grQty)
        parcel.writeString(poUom)
        parcel.writeBigDecimal(poPrice)
        parcel.writeString(poPriceUnit)
        parcel.writeBigDecimal(poAmount)
        parcel.writeString(currency)

        parcel.writeString(prNo)
        parcel.writeString(prLine)
        parcel.writeString(tracking)

        parcel.writeString(materialDocNo)
        parcel.writeString(materialDocYear)
        parcel.writeString(materialDocLine)
        parcel.writeDate(materialDocEntryDate)
        parcel.writeDate(materialDocPostingDate)
        parcel.writeBigDecimal(materialDocPrice)
        parcel.writeInt(materialDocQty)
        parcel.writeString(materialDocQtyUnit)
        parcel.writeBigDecimal(materialDocAmount)
        parcel.writeString(materialDocCurrency)

        parcel.writeString(invoiceNo)
        parcel.writeString(acctAsgmtCategory)
        parcel.writeString(contactPerson)
        parcel.writeString(purchasingOrg)
        parcel.writeString(purchasingGroup)
        parcel.writeString(materialCode)
        parcel.writeString(materialDesc)
        parcel.writeString(manufacturer)
        parcel.writeString(manufacturerDesc)
        parcel.writeString(plant)
        parcel.writeString(storageLocation)
        parcel.writeString(materialGroupDesc)
        parcel.writeString(glAcct)
        parcel.writeString(glAcctDesc)
        parcel.writeString(wbsElement)
        parcel.writeString(costCenter)
        parcel.writeDate(deliveryDate)
        parcel.writeString(taxCode)
        parcel.writeString(ers)


    }

    companion object CREATOR : Parcelable.Creator<PartPurchase> {
        override fun createFromParcel(parcel: Parcel): PartPurchase {
            return PartPurchase(parcel)
        }

        override fun newArray(size: Int): Array<PartPurchase?> {
            return arrayOfNulls(size)
        }
    }
}