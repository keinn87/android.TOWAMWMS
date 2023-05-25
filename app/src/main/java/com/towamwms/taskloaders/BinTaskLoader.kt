package com.towamwms.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import android.util.Log
import com.towamwms.Preferences
import com.towamwms.entities.Bin
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.towamwms.models.FetchParam
import com.globalsion.taskloaders.AbstractTaskLoader
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.BinApiService

@Suppress("CanBePrimaryConstructorProperty")
class BinTaskLoader(context: Context, args: Bundle?) :
        AbstractTaskLoader<TaskLoaderResult<Array<Bin>>>(context, args) {

    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<Array<Bin>> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }
            val param = FetchParam.fromBundle(arguments!!)
            val service = BinApiService(rootUrl)
            val value = service.getRecords(param)
            return TaskLoaderResult(true, value, null)
        } catch (e: Exception) {
            Log.d(BinTaskLoader::class.java.name, e.message, e)
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<Array<Bin>>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<Array<Bin>>> {
            return BinTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<Array<Bin>>>) {
        }
    }
}