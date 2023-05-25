package com.globalsion.dialogs

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import com.globalsion.dialogs.interfaces.DialogResultInterface
import java.lang.ref.WeakReference

/**
 * Extends the dialog with able to return the result back to the parent.
 * @see AbstractDialog.requestCode
 **/
abstract class AbstractDialog : AppCompatDialogFragment() {
    companion object {
        const val STATE_REQUEST_CODE = "REQUEST_CODE"
    }

    protected var weakActivity: WeakReference<Activity>? = null
    /** Identifier when return result to the parent. */
    var requestCode: Int = -1

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_REQUEST_CODE, requestCode)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        requestCode = savedInstanceState?.getInt(STATE_REQUEST_CODE) ?: requestCode
    }

    override fun onAttach(context: Context?) {
        if (context is Activity) {
            weakActivity = WeakReference(context)
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        weakActivity = null
        super.onDetach()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (requestCode >= 0) {
            val activity = weakActivity?.get()

            if (activity != null) {
                if (activity as DialogResultInterface? != null) {
                    onDialogResult(activity)
                }
            }
            if (parentFragment != null) {
                if (parentFragment as DialogResultInterface? != null) {
                    onDialogResult(parentFragment as DialogResultInterface)
                }
            }
        }

        super.onDismiss(dialog)
    }

    open fun onDialogResult(callback: DialogResultInterface) {
        callback.onDialogResult(this, requestCode, arguments)
    }
}