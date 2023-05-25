package com.globalsion.network.enums

import com.google.gson.annotations.SerializedName

enum class EnumApiPacketResult constructor(val value: Int) {
    @SerializedName("0")
    TRUE(0),
    @SerializedName("1")
    FALSE(1),
    @SerializedName("2")
    FAULTED(2);
}
