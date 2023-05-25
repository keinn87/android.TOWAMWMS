package com.towamwms.viewmodels

import android.app.Application
import android.databinding.ObservableField
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.globalsion.models.ActionLiveEvent
import com.globalsion.viewmodels.AbstractVM
import com.globalsion.views.SimpleDropDownEditText
import com.towamwms.entities.Picking
import com.towamwms.enums.EnumPickingType

class PickingActivityVM(application: Application) : AbstractVM(application),
        TextView.OnEditorActionListener, SimpleDropDownEditText.OnItemSelectedListener {

    var pickingTypes = EnumPickingType.values().map { it.description }.toTypedArray()

    var barcode = ObservableField("")
    var pickingType = ObservableField(EnumPickingType.SINGLE)
    var picking = ObservableField(null as Picking?)

    var commandPickingTypeSelected = ActionLiveEvent<EnumPickingType>()
    var commandLoadBarcode = ActionLiveEvent<String>()
    var commandInsertRecord = ActionLiveEvent<Any>()

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED &&
                    event != null && event.action != KeyEvent.ACTION_UP &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return true
            }

            commandLoadBarcode.call(barcode.get() ?: "")
            return true
        }
        return false
    }

    override fun onItemSelected(position: Int, selected: String) {
        val pickingType = EnumPickingType.values().single { it.description == selected }
        commandPickingTypeSelected.call(pickingType)
    }
}