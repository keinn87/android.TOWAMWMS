package com.towamwms.enums

import com.globalsion.network.annotations.SerializedTypeName
import com.google.gson.annotations.SerializedName

@SerializedTypeName(["com.network.enumerator.EnumAccessRight"])
enum class EnumAccessRight constructor(value: Int) {
    @SerializedName("0")
    VIEW(0),
    @SerializedName("1")
    INSERT(1),
    @SerializedName("2")
    UPDATE(2),
    @SerializedName("3")
    DELETE(3),
    @SerializedName("4")
    PRINT(4),
    @SerializedName("5")
    BRANCH(5);

    var value: Int = 0
        internal set

    init {
        this.value = value
    }

    companion object {
        @Throws(Exception::class)
        fun fromValue(value: Int): EnumAccessRight {
            return if (value == VIEW.value) {
                VIEW
            } else if (value == INSERT.value) {
                INSERT
            } else if (value == UPDATE.value) {
                UPDATE
            } else if (value == DELETE.value) {
                DELETE
            } else if (value == PRINT.value) {
                PRINT
            } else if (value == BRANCH.value) {
                BRANCH
            } else {
                throw Exception("Unknown EnumAccessRight")
            }
        }
    }
}
