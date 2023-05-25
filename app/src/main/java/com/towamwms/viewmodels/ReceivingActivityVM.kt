package com.towamwms.viewmodels

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.globalsion.models.ActionLiveEvent
import com.globalsion.viewmodels.AbstractVM
import com.towamwms.R
import com.towamwms.entities.Receiving

class ReceivingActivityVM(application: Application) : AbstractVM(application), TextView.OnEditorActionListener {

    var receiving = ObservableField(null as Receiving?)

    var partBarcode = ObservableField("")
    var binCode = ObservableField("")

    var commandLoadPart = ActionLiveEvent<String>()
    var commandLoadBin = ActionLiveEvent<String>()
    var commandInsertRecord = ActionLiveEvent<Any>()

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_NONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED &&
                    event != null && event.action != KeyEvent.ACTION_UP &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return true
            }

            when (v!!.id) {
                R.id.edit_part_barcode -> commandLoadPart.call(partBarcode.get() ?: "")
                R.id.edit_bin -> commandLoadBin.call(binCode.get() ?: "")
            }
        }
        return false
    }
}