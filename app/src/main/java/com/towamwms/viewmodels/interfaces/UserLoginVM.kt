package com.towamwms.viewmodels.interfaces

import android.databinding.ObservableField
import com.globalsion.models.ActionLiveEvent

interface UserLoginVM {
    var loginUsername : ObservableField<String>
    var loginPassword : ObservableField<String>

    var commandPerformLogin : ActionLiveEvent<Pair<String?, String?>>

    fun callCommandPerformLogin() {
        commandPerformLogin.call(Pair(loginUsername.get(), loginPassword.get()))
    }
}
