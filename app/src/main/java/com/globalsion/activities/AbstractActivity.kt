package com.globalsion.activities

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import com.globalsion.annotations.processors.AutoBundledProcessor
import com.globalsion.dialogs.LoadingDialog

/**
 * Base activity
 **/
abstract class AbstractActivity : AppCompatActivity() {
    companion object {
        const val DIALOG_LOADING = "LOADING_DIALOG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            AutoBundledProcessor.load(savedInstanceState, this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        AutoBundledProcessor.save(outState!!, this)
    }

    /**
     * Show loading dialog.
     * @param message Message to display in the dialog.
     * @param requestCode Callback identifier to the parent and must implement [com.globalsion.dialogs.interfaces.DialogResultInterface].
     **/
    fun showLoadingDialog(message: String, requestCode: Int = -1) {
        var dialog = supportFragmentManager
                .findFragmentByTag(DIALOG_LOADING) as LoadingDialog?
        if (dialog == null) {
            dialog = LoadingDialog.newInstance()
            dialog.isCancelable = false
            dialog.message = message
            dialog.requestCode = requestCode
            dialog.show(supportFragmentManager, DIALOG_LOADING)
        }
    }
    /**
     * Show loading dialog
     * @param resId String resource id, message to display in the dialog.
     * @param requestCode Identifier when callback to the parent when dialog is dismissed.
     * Parent must implement [com.globalsion.dialogs.interfaces.DialogResultInterface].
     **/
    fun showLoadingDialog(@StringRes resId: Int, requestCode: Int = -1) {
        showLoadingDialog(getString(resId), requestCode)
    }

    /**
     * Dismiss the loading dialog.
     **/
    fun dismissLoadingDialog() {
        val dialog = supportFragmentManager
                .findFragmentByTag(DIALOG_LOADING) as LoadingDialog?
        dialog?.dismiss()
    }
}