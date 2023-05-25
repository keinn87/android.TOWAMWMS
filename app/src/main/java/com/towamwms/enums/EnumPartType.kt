package com.towamwms.enums

import com.globalsion.network.annotations.SerializedTypeName
import com.google.gson.annotations.SerializedName

@SerializedTypeName(["com.towa.enumerator.EnumPartType"])
enum class EnumPartType(
        var value: Int,
        var description: String
) {
    @SerializedName("0")
    NONPO(0, "Non-PO"),
    @SerializedName("1")
    PO(1, "PO"),
    @SerializedName("2")
    STOCK(2, "Stock");

    companion object {
        fun valueOf(value: Int): EnumPartType {
            return EnumPartType.values().single {
                it.value == value
            }
        }
    }
}