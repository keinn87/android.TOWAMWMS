package com.globalsion.activities

import android.os.Bundle
import com.globalsion.viewmodels.AbstractVM

/**
 * Base activity that force the implementation of view model.
 **/
abstract class AbstractVMActivity<VM : AbstractVM> : AbstractActivity() {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateViewModel(savedInstanceState)
        onCreateDataBinding(savedInstanceState)
    }

    abstract fun onCreateViewModel(savedInstanceState: Bundle?)
    open fun onCreateDataBinding(savedInstanceState: Bundle?) { }
}