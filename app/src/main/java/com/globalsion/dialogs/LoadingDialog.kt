package com.globalsion.dialogs

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.support.v4.widget.ContentLoadingProgressBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.towamwms.R

/** Dialog that contains loading progress bar. */
class LoadingDialog : AbstractDialog() {
    companion object {
        private const val ARGS_MESSAGE = "MESSAGE"

        /** Create a new loading dialog instance. */
        fun newInstance(): LoadingDialog {
            val dialog = LoadingDialog()
            dialog.arguments = Bundle()
            return dialog
        }
    }

    private var progressBar: ContentLoadingProgressBar? = null
    private var textMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_NoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_loading, container, false)

        progressBar = rootView.findViewById(R.id.progress_bar)!!
        textMessage = rootView.findViewById(R.id.message)!!
        textMessage!!.text = arguments!!.getString(ARGS_MESSAGE)

        return rootView
    }

    /** Message to display. */
    var message: String
        get() = arguments!!.getString(ARGS_MESSAGE)
        set(value) {
            if (textMessage == null) {
                Handler(Looper.getMainLooper()).post {
                    textMessage?.text = value
                }
            } else {
                textMessage?.text = value
            }

            arguments!!.putString(ARGS_MESSAGE, value)
        }

}