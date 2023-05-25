package com.towamwms.enums

import com.globalsion.network.annotations.SerializedTypeName
import com.google.gson.annotations.SerializedName

@SerializedTypeName(["com.towa.enumerator.EnumPartStatus"])
enum class EnumPartStatus(
        var value: Int,
        var description: String
) {
    @SerializedName("0")
    OPEN(0, "Open"),
    @SerializedName("1")
    PENDINGCANCEL(1, "Pending-Cancel"),
    @SerializedName("2")
    CANCELLED(2, "Cancelled"),
    @SerializedName("3")
    RECEIVED(3, "Received"),
    @SerializedName("4")
    REQUESTTREATMENT(4, "Requesting-Treatment"),
    @SerializedName("5")
    TREATMENT(5, "Under-Treatment"),
    @SerializedName("6")
    DISTRIBUTEREADY(6, "Distribute-Ready"),
    @SerializedName("7")
    DISTRIBUTED(7, "Distributed"),
    @SerializedName("8")
    QUALITYCONTROL(8, "Quality-Control"),
    @SerializedName("9")
    PICKED(9, "Picked");

    companion object {
        fun valueOf(value: Int): EnumPartStatus {
            return EnumPartStatus.values().single {
                it.value == value
            }
        }
    }
}