package com.globalsion.utils

import android.os.Build
import android.text.Html
import android.text.Spanned

object StringUtil {
    @Suppress("LiftReturnOrAssignment", "DEPRECATION")
    @JvmStatic
    fun fromHtml(html: String) : Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            return Html.fromHtml(html)
        }
    }

    @JvmStatic
    fun htmlBold(value: String) : Spanned {
        return fromHtml("<b>$value</b>")
    }

}