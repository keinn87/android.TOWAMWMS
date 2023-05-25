package com.towamwms.activities

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.ViewGroup
import com.globalsion.activities.AbstractVMActivity
import com.globalsion.helpers.SnackbarHelper
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.globalsion.utils.AndroidUtil
import com.globalsion.viewmodels.AbstractVM
import com.towamwms.Assets
import com.towamwms.R

abstract class AbstractTowaActivity<VM : AbstractVM> : AbstractVMActivity<VM>() {
    companion object {
        const val STATE_SNACKBAR = "SNACKBAR"

    }

    val snackbar = SnackbarHelper()
    abstract val container: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val snackbarBundle = savedInstanceState?.getBundle(STATE_SNACKBAR)
        snackbar.restoreInstanceState(container, snackbarBundle)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBundle(STATE_SNACKBAR, snackbar.saveInstanceState())
    }

    fun showLoaderException(e: Exception?) {
        if (e == null) return

        if (e is RootUrlEmptyException) {
            showError(R.string.message_please_setup_root_url)
        } else {
            showError(e.message ?: "")
        }
    }

    fun showError(@StringRes resId: Int) {
        showError(resId, Assets.ERROR)
    }

    fun showError(@StringRes resId: Int, soundAssetName: String) {
        snackbar.show(container, resId, Snackbar.LENGTH_INDEFINITE)

        if (!TextUtils.isEmpty(soundAssetName)) {
            AndroidUtil.playSoundAsset(this, soundAssetName)
        }
    }

    fun showError(text: String) {
        showError(text, Assets.ERROR)
    }

    fun showError(text: String, soundAssetName: String) {
        snackbar.show(container, text, Snackbar.LENGTH_INDEFINITE)

        if (!TextUtils.isEmpty(soundAssetName)) {
            AndroidUtil.playSoundAsset(this, soundAssetName)
        }
    }
}