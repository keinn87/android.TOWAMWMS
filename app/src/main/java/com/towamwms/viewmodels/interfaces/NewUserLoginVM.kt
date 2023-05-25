package com.towamwms.viewmodels.interfaces

import android.databinding.ObservableField
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.globalsion.models.ActionLiveEvent

interface NewUserLoginVM: TextView.OnEditorActionListener {
    var loginNewUsername : ObservableField<String>


    var commandPerformNewLogin : ActionLiveEvent<String?>


 /*   override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED &&
                    event != null && event.action != KeyEvent.ACTION_UP &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return true
            }

            commandPerformNewLogin.call(loginNewUsername.get() ?: "")
            return true
        }
        return false
    }*/

    fun callCommandPerformNewLogin() {
        commandPerformNewLogin.call(loginNewUsername.get())
    }
}
