package com.globalsion.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    /**
     * Get today's date, without timestamp.
     * @return Today's date
     **/
    fun today() : Date {
        val pattern = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.parse(dateFormat.format(Date()))
    }

    /**
     * Format date to string.
     * @param pattern Format pattern.
     * @param date Date to format.
     * @return formatted string
     **/
    @Suppress("unused")
    @JvmStatic
    fun format(pattern: String, date: Date?) : String {
        if (date == null) return ""

        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(date)
    }
}