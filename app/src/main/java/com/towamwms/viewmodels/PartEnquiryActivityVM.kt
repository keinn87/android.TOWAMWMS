package com.towamwms.viewmodels

import android.app.Application
import android.databinding.ObservableField
import android.support.annotation.IdRes
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.globalsion.models.ActionLiveEvent
import com.globalsion.viewmodels.AbstractVM
import com.towamwms.entities.Part

class PartEnquiryActivityVM(application: Application) : AbstractVM(application), TextView.OnEditorActionListener {
    var partBarcode = ObservableField("")
    var part = ObservableField(null as Part?)

    var commandLoadJobSummaryView = ActionLiveEvent<Int>()

    var commandLoadDistributionReadyBin = ActionLiveEvent<Int>()

    var commandLoadPart = ActionLiveEvent<String>()
    fun callJobSummaryViewButton(@IdRes viewId: Int) = commandLoadJobSummaryView.call(viewId)
    fun callDistributionReadyBinButton(@IdRes viewId: Int) = commandLoadDistributionReadyBin.call(viewId)

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