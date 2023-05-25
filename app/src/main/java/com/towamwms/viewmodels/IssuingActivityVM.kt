package com.towamwms.viewmodels

import android.app.Application
import android.databinding.ObservableField
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.globalsion.models.ActionLiveEvent
import com.globalsion.viewmodels.AbstractVM
import com.towamwms.R
import com.towamwms.entities.Issuing
import com.towamwms.viewmodels.interfaces.NewUserLoginVM
import com.towamwms.viewmodels.interfaces.UserLoginVM

class IssuingActivityVM(application: Application) : AbstractVM(application), UserLoginVM, NewUserLoginVM, TextView.OnEditorActionListener {
    companion object {
        const val EDITOR_FOCUS = 1
        const val EDITOR_SELECT_ALL = 2
        const val EDITOR_ERROR_SET = 3
        const val EDITOR_ERROR_CLEAR = 4
    }

    var issuing = ObservableField(null as Issuing?)

    var partBarcode = ObservableField("")
    var lineOfItems = ObservableField("")
    override var loginUsername = ObservableField("")
    override var loginPassword = ObservableField("")
    override var loginNewUsername = ObservableField("")

    var commandLoadPicklist = ActionLiveEvent<String>()
    var commandInsertRecord = ActionLiveEvent<Any>()

    var commandEditPartBarcode = ActionLiveEvent<Pair<Int, Any?>>()
    var commandReloadPicklistDetails = ActionLiveEvent<Any>()
    override var commandPerformLogin = ActionLiveEvent<Pair<String?, String?>>()
    override var commandPerformNewLogin = ActionLiveEvent<String?>()

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED &&
                    event != null && event.action != KeyEvent.ACTION_UP &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return true
            }

            when (v!!.id) {
                R.id.edit_part_barcode -> {
                    commandLoadPicklist.call(partBarcode.get() ?: "")
                }

                R.id.edit_newuserlogin -> {
                    commandPerformNewLogin.call(loginNewUsername.get() ?:"")
                }
            }
            return true
        }
        return false
    }
}