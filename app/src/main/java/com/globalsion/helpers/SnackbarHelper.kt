package com.globalsion.helpers

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.annotation.*
import android.support.annotation.IntRange
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.text.format.DateUtils.LENGTH_SHORT
import android.view.View
import android.widget.TextView
import java.lang.ref.WeakReference

@Suppress("unused")
class SnackbarHelper {

    @Suppress("DEPRECATION")
    @IntDef(LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG)
    @IntRange(from = 1)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Duration

    companion object {
        const val STATE_IS_SHOWN_OR_QUEUED = "IS_SHOWN_OR_QUEUED"
        const val STATE_TEXT = "TEXT"
        const val STATE_ACTION = "ACTION"
        const val STATE_DURATION = "DURATION"
    }

    private var snackbar: Snackbar? = null
    private var weakView = WeakReference(null as View?)

    private var internalText: CharSequence? = null
    var text: CharSequence?
        get() = internalText
        set(value) {
            snackbar?.setText(text ?: "")
            internalText = value
        }

    val isShown: Boolean
        get() = snackbar?.isShown ?: false

    val isShownOrQueued: Boolean
        get() = snackbar?.isShownOrQueued ?: false

    fun show(view: View, text: CharSequence, @Duration duration: Int) {
        if (snackbar == null || weakView.get() != view) {
            snackbar = Snackbar.make(view, text, duration)
            internalText = text
            weakView = WeakReference(view)

            val textView: TextView = snackbar!!.view
                    .findViewById(android.support.design.R.id.snackbar_text)
            textView.maxLines = 8
        } else {
            snackbar!!.setText(text)
            internalText = text
        }

        if (!snackbar!!.isShownOrQueued) {
            snackbar!!.show()
        }
    }

    fun show(view: View, @StringRes resId: Int, @Duration duration: Int) {
        if (snackbar == null || weakView.get() != view) {
            snackbar = Snackbar.make(view, resId, duration)
            internalText = view.context.getText(resId)
            weakView = WeakReference(view)

            val textView: TextView = snackbar!!.view
                    .findViewById(android.support.design.R.id.snackbar_text)
            textView.maxLines = 8
        } else {
            snackbar!!.setText(resId)
            internalText = view.context.getText(resId)
        }

        if (!snackbar!!.isShownOrQueued) {
            snackbar!!.show()
        }
    }

    fun setAction(text: CharSequence, listener: (View) -> Unit) {
        snackbar?.setAction(text, listener)
    }

    fun setAction(text: CharSequence, listener: View.OnClickListener) {
        snackbar?.setAction(text, listener)
    }

    fun setAction(@StringRes resId: Int, listener: (View) -> Unit) {
        snackbar?.setAction(resId, listener)
    }

    fun setAction(@StringRes resId: Int, listener: View.OnClickListener) {
        snackbar?.setAction(resId, listener)
    }

    fun setActionTextColor(colors: ColorStateList?) {
        snackbar?.setActionTextColor(colors)
    }

    fun setActionTextColor(@ColorInt color: Int) {
        snackbar?.setActionTextColor(color)
    }

    fun dismiss() {
        snackbar?.dismiss()
    }

    fun saveInstanceState(): Bundle {
        val bundle = Bundle()

        bundle.putBoolean(STATE_IS_SHOWN_OR_QUEUED, isShownOrQueued)
        bundle.putString(STATE_TEXT, text?.toString())
        bundle.putInt(STATE_DURATION, snackbar?.duration ?: 0)

        return bundle
    }

    fun restoreInstanceState(view: View, bundle: Bundle?) {
        if (bundle == null) {
            return
        }

        val isShownOrQueued = bundle.getBoolean(STATE_IS_SHOWN_OR_QUEUED, false)
        val text = bundle.getString(STATE_TEXT, null)

        if (isShownOrQueued) {
            val duration = bundle.getInt(STATE_DURATION, 0)
            show(view, text, duration)
        }
    }
}