package com.towamwms.viewmodels

import android.app.Application
import android.databinding.ObservableField
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.globalsion.models.ActionLiveEvent
import com.globalsion.viewmodels.AbstractVM
import com.globalsion.views.SimpleDropDownEditText
import com.towamwms.R
import com.towamwms.entities.Transferring
import com.towamwms.enums.EnumTransferringType

class TransferringActivityVM(application: Application) : AbstractVM(application),
        TextView.OnEditorActionListener, SimpleDropDownEditText.OnItemSelectedListener {

    var transferringTypes = EnumTransferringType.values().map { it.description }.toTypedArray()

    var transferring = ObservableField(null as Transferring?)
    var barcode = ObservableField("")
    var destinationBinCode = ObservableField("")

    var commandLoadBarcode = ActionLiveEvent<String>()
    var commandLoadDestinationBin = ActionLiveEvent<String>()
    var commandInsertRecord = ActionLiveEvent<Any>()
    var commandTransferringTypeSelected = ActionLiveEvent<EnumTransferringType>()

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED &&
                    event != null && event.action != KeyEvent.ACTION_UP &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return true
            }

            when (v!!.id) {
                R.id.edit_barcode -> commandLoadBarcode.call(barcode.get() ?: "")
                R.id.edit_destination_bin_code -> commandLoadDestinationBin.call(destinationBinCode.get() ?: "")
            }
        }
        return false
    }

    override fun onItemSelected(position: Int, selected: String) {
        val transferringType = EnumTransferringType.values().single { it.description == selected }
        commandTransferringTypeSelected.call(transferringType)
    }
}