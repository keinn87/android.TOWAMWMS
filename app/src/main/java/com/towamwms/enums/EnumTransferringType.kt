package com.towamwms.enums

import com.globalsion.network.annotations.SerializedTypeName
import com.google.gson.annotations.SerializedName

@SerializedTypeName(["com.towa.enumerator.EnumTransferringType"])
enum class EnumTransferringType(
        var value: Int,
        var description: String
) {
    @SerializedName("0")
    SINGLE(0, "Single-Item"),
    @SerializedName("1")
    BULK(1, "Whole-Bin/Pallet");

    companion object {
        fun valueOf(value: Int) : EnumTransferringType {
            return EnumTransferringType.values().single {
                it.value == value
            }
        }
    }
}