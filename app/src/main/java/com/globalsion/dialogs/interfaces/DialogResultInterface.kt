package com.globalsion.dialogs.interfaces

import android.os.Bundle
import com.globalsion.dialogs.AbstractDialog

/** Interface to provide the callback method [onDialogResult] for [AbstractDialog] */
interface DialogResultInterface {
    /** Callback result used by [AbstractDialog] when [AbstractDialog.requestCode] is set. */
    fun onDialogResult(dialog: AbstractDialog, requestCode: Int, data: Bundle?)
}