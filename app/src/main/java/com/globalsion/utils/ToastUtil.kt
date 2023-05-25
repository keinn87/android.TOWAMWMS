package com.globalsion.utils

import android.content.Context
import android.support.annotation.IntDef
import android.support.annotation.StringRes
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT

/**
 * Provide singleton (prevent overlapping) design to showing toast.
 */
object ToastUtil {
    /** @hide */
    @IntDef(LENGTH_SHORT, LENGTH_LONG)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Duration

    var oldToast: Toast? = null

    fun show(context: Context, text: CharSequence, @Duration duration: Int) {
        val toast = Toast.makeText(context, text, duration)
        oldToast?.cancel()
        toast.show()
        oldToast = toast
    }

    fun show(context: Context, @StringRes resId: Int, @Duration duration: Int) {
        val toast = Toast.makeText(context, resId, duration)
        oldToast?.cancel()
        toast.show()
        oldToast = toast
    }
}