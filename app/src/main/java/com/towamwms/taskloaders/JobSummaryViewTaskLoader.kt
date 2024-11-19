package com.towamwms.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import android.util.Log
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.globalsion.taskloaders.AbstractTaskLoader
import com.towamwms.Preferences
import com.towamwms.entities.JobSummaryView
import com.towamwms.models.FetchParam
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.JobSummaryViewApiService

@Suppress("CanBePrimaryConstructorProperty")
class JobSummaryViewTaskLoader(context: Context, args: Bundle?) :
        AbstractTaskLoader<TaskLoaderResult<Array<JobSummaryView>>>(context, args) {

    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<Array<JobSummaryView>> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }
            val param = FetchParam.fromBundle(arguments!!)
            val service = JobSummaryViewApiService(rootUrl)
            val value = service.getRecords(param)
            return TaskLoaderResult(true, value, null)
        } catch (e: Exception) {
            Log.d(JobSummaryViewTaskLoader::class.java.name, e.message, e)
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<Array<JobSummaryView>>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<Array<JobSummaryView>>> {
            return JobSummaryViewTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<Array<JobSummaryView>>>) {
        }
    }
}