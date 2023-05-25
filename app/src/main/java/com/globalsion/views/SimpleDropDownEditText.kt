package com.globalsion.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import com.globalsion.utils.AndroidUtil
import com.towamwms.R
import com.globalsion.views.abstracts.AbstractDialogEditText

class SimpleDropDownEditText : AbstractDialogEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inputType = InputType.TYPE_NULL

        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_grey_24dp, 0)
        setPaddingRelative(paddingStart, paddingTop, AndroidUtil.convertDpToPixel(12f).toInt(), paddingBottom)
        isLongClickable = false
    }

    var selectedItem: String
        get() = text.toString()
        set(value) = setText(value)
    var selectedPosition: Int
        get() = items.indexOf(text.toString())
        set(value) = setText(items[value])

    val items = arrayListOf<String>()
    private var dialog: AlertDialog? = null
    private var onItemSelectedListener: OnItemSelectedListener? = null

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        onItemSelectedListener = listener
    }

    fun setOnItemSelectedListener(listener: (position: Int, selected: String) -> Unit) {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(position: Int, selected: String) {
                listener(position, selected)
            }
        }
    }

    override fun showDialog() {
        if (items.isEmpty()) {
            return
        }

        var hint = this.hint

        if (TextUtils.isEmpty(hint)) {
            hint = findTextInputLayout()?.hint
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle(hint)
        builder.setItems(items.toTypedArray()) { _, which ->
            setText(items[which])
            onItemSelectedListener?.onItemSelected(which, items[which])
        }
        dialog = builder.show()
    }

    override fun closeDialog() {
        Handler(Looper.getMainLooper()).post {
            dialog?.dismiss()
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int, selected: String)
    }
}