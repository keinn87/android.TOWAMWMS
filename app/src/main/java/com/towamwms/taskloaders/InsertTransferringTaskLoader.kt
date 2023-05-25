package com.towamwms.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import com.towamwms.Preferences
import com.towamwms.entities.Transferring
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.towamwms.models.InsertParam
import com.globalsion.taskloaders.AbstractTaskLoader
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.TransferringApiService

class InsertTransferringTaskLoader(context: Context, args: Bundle?) :
        AbstractTaskLoader<TaskLoaderResult<Transferring>>(context, args) {

    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<Transferring> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }
            val param = InsertParam.fromBundle<Transferring>(arguments!!)
            val service = TransferringApiService(rootUrl)
            val transferring = service.insertRecord(param.user, param.entity)

            return TaskLoaderResult(true, transferring, null)
        } catch (e: Exception) {
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<Transferring>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<Transferring>> {
            return InsertTransferringTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<Transferring>>) {
        }
    }
}