package com.towamwms.dialogs

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.globalsion.dialogs.AbstractDialog
import com.globalsion.dialogs.SimpleFilterDialog
import com.globalsion.utils.AndroidUtil
import com.globalsion.utils.ClassUtil
import com.towamwms.R
import com.towamwms.databinding.DialogUserLoginBinding
import com.towamwms.viewmodels.interfaces.UserLoginVM

class UserLoginDialog : AbstractDialog() {
    companion object {
        const val ARGS_TITLE = "TITLE"
        const val ARGS_CLAZZ_VIEW_MODEL = "CLAZZ_VIEW_MODEL"

        fun <T> newInstance(title: String, clazzViewModel : Class<T>) : UserLoginDialog
            where T : ViewModel, T : UserLoginVM {

            val dialog = UserLoginDialog()
            dialog.arguments = Bundle()
            dialog.arguments!!.putString(ARGS_TITLE, title)
            dialog.arguments!!.putString(ARGS_CLAZZ_VIEW_MODEL, clazzViewModel.name)

            return dialog
        }
    }

    lateinit var viewModel: UserLoginVM
    lateinit var binding: DialogUserLoginBinding

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val clazzViewModel = ClassUtil.forName(arguments!!.getString(ARGS_CLAZZ_VIEW_MODEL))
        val activity = weakActivity?.get() as FragmentActivity?

        if (activity == null) {
            dismiss()
            return null
        }

        dialog.setTitle(arguments!!.getString(SimpleFilterDialog.ARGS_TITLE))
        val textTitle: TextView = dialog.findViewById(android.support.v4.R.id.title)
        textTitle.setPadding(textTitle.paddingLeft, textTitle.paddingTop, textTitle.paddingRight,
                AndroidUtil.convertDpToPixel(16f).toInt())

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_user_login, container, false)
        viewModel = ViewModelProviders.of(activity).get(clazzViewModel as Class<ViewModel>) as UserLoginVM
        binding.viewModel = viewModel
        binding.buttonCancel.setOnClickListener { dismiss() }
        binding.buttonLogin.setOnClickListener { viewModel.callCommandPerformLogin() }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val attributes = dialog.window.attributes
        attributes.horizontalMargin = 46f
        dialog.window.attributes = attributes
    }
}