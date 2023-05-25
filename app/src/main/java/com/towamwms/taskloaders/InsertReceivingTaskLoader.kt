package com.towamwms.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import com.towamwms.Preferences
import com.towamwms.entities.Receiving
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.towamwms.models.InsertParam
import com.globalsion.taskloaders.AbstractTaskLoader
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.ReceivingApiService

class InsertReceivingTaskLoader(context: Context, args: Bundle?) :
        AbstractTaskLoader<TaskLoaderResult<Receiving>>(context, args) {

    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<Receiving> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }
            val param = InsertParam.fromBundle<Receiving>(arguments!!)
            val service = ReceivingApiService(rootUrl)
            val receiving = service.insertRecord(param.user, param.entity)

            return TaskLoaderResult(true, receiving, null)
        } catch (e: Exception) {
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<Receiving>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<Receiving>> {
            return InsertReceivingTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<Receiving>>) {
        }
    }
}