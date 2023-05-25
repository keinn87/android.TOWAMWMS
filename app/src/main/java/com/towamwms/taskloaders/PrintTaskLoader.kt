package com.towamwms.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import com.towamwms.Preferences
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.towamwms.models.PrintParam
import com.globalsion.taskloaders.AbstractTaskLoader
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.PrintApiService

class PrintTaskLoader(context: Context, args: Bundle?) : AbstractTaskLoader<TaskLoaderResult<Boolean>>(context, args) {
    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<Boolean> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }
            val param = PrintParam.fromBundle(arguments!!)
            val service = PrintApiService(rootUrl)
            val value = service.print(param)

            return TaskLoaderResult(true, value, null)
        } catch (e: Exception) {
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<Boolean>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<Boolean>> {
            return PrintTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<Boolean>>) {
        }
    }
}