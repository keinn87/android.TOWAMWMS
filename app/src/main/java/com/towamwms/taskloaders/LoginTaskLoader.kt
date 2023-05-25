package com.towamwms.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import android.util.Log
import com.towamwms.Preferences
import com.globalsion.taskloaders.AbstractTaskLoader
import com.towamwms.entities.User
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.UserApiService

class LoginTaskLoader(context: Context, args: Bundle?) : AbstractTaskLoader<TaskLoaderResult<User>>(context, args) {
    companion object {
        const val ARGS_USERNAME = "USERNAME"
        const val ARGS_PASSWORD = "PASSWORD"
    }

    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<User> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }

            val username = arguments!!.getString(ARGS_USERNAME)
            val password = arguments.getString(ARGS_PASSWORD)
            val service = UserApiService(rootUrl)
            val user = service.login(username, password)

            return TaskLoaderResult(true, user, null)
        } catch (e: Exception) {
            Log.e(LoginTaskLoader::class.java.name, e.message, e)
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<User>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<User>> {
            return LoginTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<User>>) {
        }
    }
}