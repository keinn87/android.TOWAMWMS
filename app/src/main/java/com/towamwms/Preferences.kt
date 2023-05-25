package com.towamwms

import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager

object Preferences {
    const val API_SERVER_URL = "PREF_API_SERVER_URL"
    const val API_PACKET_COMPRESSION = "PREF_API_PACKET_COMPRESSION"
    const val API_PACKET_LOG_COMPRESSION_RATE = "PREF_API_PACKET_LOG_COMPRESSION_RATE"
    const val API_PACKET_LOG_RAW_JSON = "PREF_API_PACKET_LOG_RAW_JSON"
    const val PRINTER_NAME = "PREF_PRINTER_NAME"

    /** Use for save and restore application state */
    const val STATE_LOGIN_USER = "STATE_LOGIN_USER"

    fun getInstance(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(MainApplication.application)
    }
}
