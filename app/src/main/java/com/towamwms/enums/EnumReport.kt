package com.towamwms.enums

import com.globalsion.network.annotations.SerializedTypeName
import com.google.gson.annotations.SerializedName

@SerializedTypeName(["com.network.enumerator.EnumReport"])
enum class EnumReport(
        var value: Int
) {
    @SerializedName("0")
    LABEL_BARCODE_FINAL(0);

    companion object {
        fun valueOf(value: Int): EnumReport {
            return EnumReport.values().single {
                it.value == value
            }
        }
    }
}