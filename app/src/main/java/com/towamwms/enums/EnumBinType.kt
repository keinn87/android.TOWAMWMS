package com.towamwms.enums

import com.globalsion.network.annotations.SerializedTypeName
import com.google.gson.annotations.SerializedName

@SerializedTypeName(["com.towa.enumerator.EnumBinType"])
enum class EnumBinType(
        var value: Int,
        var description: String
) {
    @SerializedName("0")
    BIN(0, "Bin"),
    @SerializedName("1")
    PALLET(1, "Pallet");

    companion object {
        fun valueOf(value: Int): EnumBinType {
            return EnumBinType.values().single {
                it.value == value
            }
        }
    }
}