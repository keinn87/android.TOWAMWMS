package com.towamwms.viewmodels

import android.app.Application
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.graphics.drawable.Drawable
import android.support.annotation.IdRes
import android.view.View
import com.globalsion.models.ActionLiveEvent
import com.globalsion.viewmodels.AbstractVM

class LoginActivityVM(application: Application) : AbstractVM(application) {
    var buttonLoginText = ObservableField("")
    var buttonLoginDrawableEnd = ObservableField(null as Drawable?)
    var layoutProgressVisibility = ObservableInt(View.GONE)
    var username = ObservableField("")
    var password = ObservableField("")

    val commandButton = ActionLiveEvent<Int>()
    fun callCommandButton(@IdRes viewId: Int) = commandButton.call(viewId)
}