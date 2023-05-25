package com.towamwms.viewmodels

import android.app.Application
import android.support.annotation.IdRes
import com.globalsion.models.ActionLiveEvent
import com.globalsion.viewmodels.AbstractVM

class MainActivityVM(application: Application) : AbstractVM(application) {
    val commandButton = ActionLiveEvent<Int>()
    fun callCommandButton(@IdRes viewId: Int) = commandButton.call(viewId)
}