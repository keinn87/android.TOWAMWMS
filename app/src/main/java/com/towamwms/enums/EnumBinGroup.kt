package com.towamwms.enums

import com.globalsion.network.annotations.SerializedTypeName
import com.google.gson.annotations.SerializedName

@SerializedTypeName(["com.towa.enumerator.EnumBinGroup"])
enum class EnumBinGroup(
        var value: Int,
        var description: String
) {
    @SerializedName("0")
    STAGING(0, "Staging"),
    @SerializedName("1")
    QUALITYCONTROL(1, "Quality Control"),
    @SerializedName("2")
    DISTRIBUTIONCENTER(2, "Distribution Center");

    companion object {
        fun valueOf(value: Int): EnumBinGroup {
            return EnumBinGroup.values().single {
                it.value == value
            }
        }
    }
}
