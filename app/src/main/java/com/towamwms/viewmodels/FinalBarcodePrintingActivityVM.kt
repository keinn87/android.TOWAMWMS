package com.towamwms.viewmodels

import android.app.Application
import android.databinding.ObservableField
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.globalsion.models.ActionLiveEvent
import com.globalsion.viewmodels.AbstractVM

class FinalBarcodePrintingActivityVM(application: Application) : AbstractVM(application), TextView.OnEditorActionListener {
    var partBarcode = ObservableField("")

    var commandLoadPart = ActionLiveEvent<String>()
    var commandPrint = ActionLiveEvent<Array<Long>>()

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED &&
                    event != null && event.action != KeyEvent.ACTION_UP &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return true
            }

            commandLoadPart.call(partBarcode.get() ?: "")
            return true
        }
        return false
    }
}