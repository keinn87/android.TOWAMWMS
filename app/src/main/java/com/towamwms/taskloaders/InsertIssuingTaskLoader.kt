package com.towamwms.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import com.towamwms.Preferences
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.towamwms.models.InsertParam
import com.globalsion.taskloaders.AbstractTaskLoader
import com.towamwms.entities.Issuing
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.IssuingApiService

class InsertIssuingTaskLoader(context: Context, args: Bundle?) :
        AbstractTaskLoader<TaskLoaderResult<Issuing>>(context, args) {

    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<Issuing> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }
            val param = InsertParam.fromBundle<Issuing>(arguments!!)
            val service = IssuingApiService(rootUrl)
            val receiving = service.insertRecord(param.user, param.entity)

            return TaskLoaderResult(true, receiving, null)
        } catch (e: Exception) {
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<Issuing>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<Issuing>> {
            return InsertIssuingTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<Issuing>>) {
        }
    }
}