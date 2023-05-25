package com.globalsion.views

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import com.globalsion.dialogs.DropDownDialog
import com.globalsion.utils.ClassUtil
import com.globalsion.views.abstracts.AbstractDialogEditText
import java.util.*

class DropDownEditText : AbstractDialogEditText {
    companion object {
        private const val STATE_SUPER = "SUPER"
        private const val STATE_EXTRAS = "EXTRAS"
        private const val STATE_ADAPTER_CLASS_NAME = "ADAPTER_CLASS_NAME"
        private const val STATE_DIALOG_IDENTIFIER = "DIALOG_IDENTIFIER"
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var extras: Bundle? = null
    var adapter: Class<*>? = null
    var displayMember: String? = null
    var selectedItem: Parcelable? = null
    private var dialogIdentifier: UUID? = null
    private var onItemSelectedListener: OnItemSelectedListener? = null

    private val displayMemberGetter: String
        get() {
            var tmp = "get"

            if (displayMember != null) {
                if (displayMember!!.isNotEmpty()) {
                    tmp += displayMember!![0].toUpperCase()
                }
                if (displayMember!!.length >= 2) {
                    tmp += displayMember!!.substring(1)
                }
            }

            return tmp
        }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        onItemSelectedListener = listener
    }
    fun setOnItemSelectedLisetner(listener: (view: DropDownEditText, selected: Parcelable?) -> Boolean) {
        onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(view: DropDownEditText, selected: Parcelable?): Boolean {
                return listener(view, selected)
            }
        }
    }

    override fun showDialog() {
        if (adapter == null) {
            throw IllegalArgumentException("adapter is required.")
        }
        if (!DropDownDialog.Adapter::class.java.isAssignableFrom(adapter!!)) {
            throw IllegalArgumentException("adapter must implement '${DropDownDialog.Adapter::class.java.name}'.")
        }

        var hint = this.hint
        if (TextUtils.isEmpty(hint)) {
            hint = findTextInputLayout()?.hint
        }

        val activity = context as AppCompatActivity
        val builder = DropDownDialog.Builder()
        builder.extras = extras
        builder.title = hint?.toString() ?: ""
        builder.adapter = adapter
        val dialog = builder.create()
        dialog.setOnItemSelectedListener(dropDownDialogOnItemSelectedListener)

        if (dialogIdentifier == null) {
            do {
                dialogIdentifier = UUID.randomUUID()

                val fragment = activity.supportFragmentManager
                        .findFragmentByTag(dialogIdentifier.toString())

                @Suppress("FoldInitializerAndIfToElvis")
                if (fragment == null) {
                    break
                }
            } while (true)
        }

        val fragment = activity.supportFragmentManager
                .findFragmentByTag(dialogIdentifier.toString())
        if (fragment == null) {
            dialog.show(activity.supportFragmentManager, dialogIdentifier.toString())
        }
    }

    override fun closeDialog() {
        Handler(Looper.getMainLooper()).post {
            if (dialogIdentifier != null) {
                val activity = context as AppCompatActivity
                val fragment = activity.supportFragmentManager
                        .findFragmentByTag(dialogIdentifier.toString())
                if (fragment != null && fragment is DropDownDialog) {
                    fragment.dismiss()
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val savedState = Bundle()
        val parcelable = super.onSaveInstanceState()
        savedState.putParcelable(STATE_SUPER, parcelable)
        savedState.putBundle(STATE_EXTRAS, extras)
        savedState.putString(STATE_ADAPTER_CLASS_NAME, adapter?.name)
        savedState.putSerializable(STATE_DIALOG_IDENTIFIER, dialogIdentifier)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val parcelable = state.getParcelable(STATE_SUPER) as Parcelable?
            val adapterClassName: String? = state.getString(STATE_ADAPTER_CLASS_NAME)
            extras = state.getBundle(STATE_EXTRAS)
            adapter = if (adapterClassName.isNullOrEmpty()) null else ClassUtil.forName(adapterClassName!!)
            dialogIdentifier = state.getSerializable(STATE_DIALOG_IDENTIFIER) as UUID?

            if (dialogIdentifier != null) {
                val activity = context as AppCompatActivity
                val fragment = activity.supportFragmentManager
                        .findFragmentByTag(dialogIdentifier.toString())
                if (fragment != null && fragment is DropDownDialog) {
                    fragment.setOnItemSelectedListener(dropDownDialogOnItemSelectedListener)
                }
            }

            super.onRestoreInstanceState(parcelable)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private val dropDownDialogOnItemSelectedListener= object: DropDownDialog.OnItemSelectedListener {
        override fun onItemSelected(dialog: DropDownDialog, item: Parcelable?): Boolean {
            selectedItem = item

            if (!displayMember.isNullOrEmpty()) {
                if (selectedItem != null) {
                    try {
                        val value = selectedItem!!.javaClass
                                .getMethod(displayMemberGetter)
                                .invoke(selectedItem)

                        setText(value?.toString() ?: "")
                    } catch (e: Exception) {
                        Log.e("DropDownEditText", "error", e)
                        setText("")
                    }
                } else {
                    setText("")
                }
            }

            return false
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(view: DropDownEditText, selected: Parcelable?): Boolean
    }
}







