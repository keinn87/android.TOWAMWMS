package com.towamwms.enums

import com.globalsion.network.annotations.SerializedTypeName
import com.google.gson.annotations.SerializedName

@SerializedTypeName(["com.towa.enumerator.EnumPickingType"])
enum class EnumPickingType(
        var value: Int,
        var description: String
) {
    @SerializedName("0")
    SINGLE(0, "Single-Item"),
    @SerializedName("1")
    BULK(1, "Whole-Bin/Pallet");

    companion object {
        fun valueOf(value: Int) : EnumPickingType {
            return EnumPickingType.values().single {
                it.value == value
            }
        }
    }
}
