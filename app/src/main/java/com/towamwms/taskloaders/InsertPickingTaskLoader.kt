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
import com.towamwms.entities.Picking
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.PickingApiService

class InsertPickingTaskLoader(context: Context, args: Bundle?) :
        AbstractTaskLoader<TaskLoaderResult<Picking>>(context, args) {

    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<Picking> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }
            val param = InsertParam.fromBundle<Picking>(arguments!!)
            val service = PickingApiService(rootUrl)
            val receiving = service.insertRecord(param.user, param.entity)

            return TaskLoaderResult(true, receiving, null)
        } catch (e: Exception) {
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<Picking>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<Picking>> {
            return InsertPickingTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<Picking>>) {
        }
    }
}