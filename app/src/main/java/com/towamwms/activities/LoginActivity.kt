package com.towamwms.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.Snackbar
import android.support.v4.content.Loader
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.globalsion.utils.ToastUtil
import com.towamwms.Assets
import com.towamwms.MainApplication
import com.towamwms.R
import com.towamwms.databinding.ActivityLoginBinding
import com.towamwms.entities.User
import com.globalsion.utils.AndroidUtil
import com.towamwms.models.TaskLoaderResult
import com.towamwms.taskloaders.LoginTaskLoader
import com.towamwms.viewmodels.LoginActivityVM

class LoginActivity : AbstractTowaActivity<LoginActivityVM>() {
    companion object {
        const val LOADER_LOGIN = 1

        @Suppress("unused")
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    lateinit var binding: ActivityLoginBinding
    override val container: ViewGroup
        get() = binding.container

    override fun onCreateViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(LoginActivityVM::class.java)
        viewModel.commandButton.observe(this, Observer { commandButton(it!!) })
    }

    override fun onCreateDataBinding(savedInstanceState: Bundle?) {
        super.onCreateDataBinding(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar as Toolbar)

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        title = title.toString() + " ($versionName)"
    }

    override fun onResume() {
        super.onResume()

        val loginLoader = this.supportLoaderManager
                .getLoader<TaskLoaderResult<User>>(LOADER_LOGIN)
        if (loginLoader != null) {
            viewModel.buttonLoginText.set(getText(R.string.text_cancel).toString())
            viewModel.buttonLoginDrawableEnd.set(getDrawable(R.drawable.ic_cancel_white_24dp))
            viewModel.layoutProgressVisibility.set(View.VISIBLE)
        } else {
            viewModel.buttonLoginText.set(getText(R.string.text_login).toString())
            viewModel.buttonLoginDrawableEnd.set(getDrawable(R.drawable.ic_lock_open_grey_24dp))
            viewModel.layoutProgressVisibility.set(View.GONE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_settings -> {
                val loginLoader = supportLoaderManager
                        .getLoader<TaskLoaderResult<*>>(LOADER_LOGIN)
                if (loginLoader != null) {
                    snackbar.show(binding.container,
                            R.string.message_please_cancel_login, Snackbar.LENGTH_INDEFINITE)
                    AndroidUtil.playSoundAsset(this, Assets.ERROR)
                } else {
                    val intent = SettingsActivity.newIntent(this, false)
                    startActivity(intent)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        snackbar.show(binding.container, R.string.message_please_login, Snackbar.LENGTH_INDEFINITE)
        AndroidUtil.playSoundAsset(this, Assets.ERROR)
    }

    fun commandButton(@IdRes viewId: Int) {
        when (viewId) {
            R.id.button_login -> {
                val loginLoader = supportLoaderManager
                        .getLoader<TaskLoaderResult<*>>(LOADER_LOGIN)
                if (loginLoader == null) {
                    val args = Bundle()
                    args.putString(LoginTaskLoader.ARGS_USERNAME, viewModel.username.get())
                    args.putString(LoginTaskLoader.ARGS_PASSWORD, viewModel.password.get())

                    supportLoaderManager.initLoader(LOADER_LOGIN, args, loginLoaderCallbacks).forceLoad()
                    viewModel.buttonLoginText.set(getText(R.string.text_cancel).toString())
                    viewModel.buttonLoginDrawableEnd.set(getDrawable(R.drawable.ic_cancel_white_24dp))
                    viewModel.layoutProgressVisibility.set(View.VISIBLE)
                } else {
                    supportLoaderManager.destroyLoader(LOADER_LOGIN)
                    viewModel.buttonLoginText.set(getText(R.string.text_login).toString())
                    viewModel.buttonLoginDrawableEnd.set(getDrawable(R.drawable.ic_lock_open_grey_24dp))
                    viewModel.layoutProgressVisibility.set(View.GONE)
                }
            }
        }
    }

    private val loginLoaderCallbacks = object : LoginTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<User>>, data: TaskLoaderResult<User>) {
            supportLoaderManager.destroyLoader(LOADER_LOGIN)

            viewModel.buttonLoginText.set(getText(R.string.text_login).toString())
            viewModel.buttonLoginDrawableEnd.set(getDrawable(R.drawable.ic_lock_open_grey_24dp))
            viewModel.layoutProgressVisibility.set(View.GONE)

            if (!data.success) {
                showLoaderException(data.exception)
                return
            }
            if (data.value == null) {
                snackbar.show(binding.container, R.string.message_authentication_failed, Snackbar.LENGTH_INDEFINITE)
                AndroidUtil.playSoundAsset(this@LoginActivity, Assets.ERROR)
                return
            }

            MainApplication.loginUser = data.value

            val format = getText(R.string.message_logged_in).toString()
            ToastUtil.show(this@LoginActivity, String.format(format, data.value!!.username), Toast.LENGTH_SHORT)
            finish()
        }
    }
}