package com.globalsion.network.enums

import com.google.gson.annotations.SerializedName

enum class EnumObjectType(
        var value: Int
) {
    @SerializedName("0")
    NULL(0),
    @SerializedName("1")
    CHARACTER(1),
    @SerializedName("2")
    BYTE(2),
    @SerializedName("3")
    SHORT(3),
    @SerializedName("4")
    INTEGER(4),
    @SerializedName("5")
    LONG(5),
    @SerializedName("6")
    FLOAT(6),
    @SerializedName("7")
    DOUBLE(7),
    @SerializedName("8")
    STRING(8),
    @SerializedName("9")
    DATE(9),
    @SerializedName("10")
    BIG_DECIMAL(10),
    @SerializedName("11")
    UUID(11),
    @SerializedName("12")
    OBJECT_HOLDER(12),
    @SerializedName("21")
    CHARACTER_ARRAY(21),
    @SerializedName("22")
    BYTE_ARRAY(22),
    @SerializedName("23")
    SHORT_ARRAY(23),
    @SerializedName("24")
    INTEGER_ARRAY(24),
    @SerializedName("25")
    LONG_ARRAY(25),
    @SerializedName("26")
    FLOAT_ARRAY(26),
    @SerializedName("27")
    DOUBLE_ARRAY(27),
    @SerializedName("28")
    STRING_ARRAY(28),
    @SerializedName("29")
    DATE_ARRAY(29),
    @SerializedName("30")
    BIG_DECIMAL_ARRAY(30),
    @SerializedName("31")
    UUID_ARRAY(31),
    @SerializedName("32")
    OBJECT_HOLDER_ARRAY(32),
    @SerializedName("99")
    OBJECT(99);
}
