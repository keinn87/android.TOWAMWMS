package com.globalsion.views.abstracts

import android.content.Context
import android.graphics.Rect
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.ViewParent
import com.towamwms.R
import com.globalsion.utils.AndroidUtil

@Suppress("LeakingThis")
abstract class AbstractDialogEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inputType = InputType.TYPE_NULL

        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_grey_24dp, 0)
        setPaddingRelative(paddingStart, paddingTop, AndroidUtil.convertDpToPixel(12f).toInt(), paddingBottom)
        isLongClickable = false
    }

    /** Perform show dialog when focused, also will hide the soft keyboard if necessary. */
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (focused) {
            if (isEnabled) {
                AndroidUtil.hideKeyboard(this)
                showDialog()
            }
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    /** Perform show dialog when click. */
    override fun callOnClick(): Boolean {
        if (isEnabled) {
            showDialog()
        }
        return super.callOnClick()
    }

    /** Perform show dialog when click. */
    override fun performClick(): Boolean {
        if (isEnabled) {
            showDialog()
        }
        return super.performClick()
    }

    /** Override and disable the input key. */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false //Enable back button
        }
        return true //Exit, always disable input key.
    }

    /** Override and disable the input key. */
    override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent?): Boolean {
        return true //Exit, always disable input key
    }

    /** Override and disable the input key. */
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false //Enable back button
        }
        return true //Exit, always disable input key
    }

    /** Perform show items dialog. */
    protected abstract fun showDialog()

    /** Perform close dialog */
    abstract fun closeDialog()

    /** Find [TextInputLayout] from parent, returns null if not exists. */
    protected fun findTextInputLayout() : TextInputLayout? {
        var layout: ViewParent? = parent

        do {
            when (layout) {
                null -> return null
                is TextInputLayout -> return layout
                else -> layout = layout.parent
            }
        } while (true)
    }
}