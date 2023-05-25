package com.towamwms.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import android.util.Log
import com.towamwms.Preferences
import com.globalsion.network.exceptions.RootUrlEmptyException
import com.towamwms.models.FetchParam
import com.globalsion.taskloaders.AbstractTaskLoader
import com.towamwms.entities.Picklist
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.PicklistApiService

@Suppress("CanBePrimaryConstructorProperty")
class PicklistTaskLoader(context: Context, args: Bundle?) :
        AbstractTaskLoader<TaskLoaderResult<Array<Picklist>>>(context, args) {

    companion object {
        public const val ARGS_METHOD = "METHOD"
        public const val ARGS_PART_BARCODE = "PART_BARCODE"

        public const val METHOD_GET_RECORDS = 1
        public const val METHOD_GET_RECORD_BY_PART_BARCODE = 2
    }

    @Suppress("LiftReturnOrAssignment")
    override fun loadInBackground(): TaskLoaderResult<Array<Picklist>> {
        val rootUrl = Preferences.getInstance().getString(Preferences.API_SERVER_URL, null)

        try {
            if (TextUtils.isEmpty(rootUrl)) {
                throw RootUrlEmptyException()
            }
            val value: Array<Picklist>
            val param = FetchParam.fromBundle(arguments!!)
            val service = PicklistApiService(rootUrl)
            if (arguments.getInt(ARGS_METHOD) == METHOD_GET_RECORD_BY_PART_BARCODE) {
                val partBarcode = arguments.getString(ARGS_PART_BARCODE)
                value = service.getRecordByPartBarcode(param.user, partBarcode)
            } else {
                value = service.getRecords(param)
            }

            return TaskLoaderResult(true, value, null)
        } catch (e: Exception) {
            Log.d(PicklistTaskLoader::class.java.name, e.message, e)
            return TaskLoaderResult(false, null, e)
        }
    }

    abstract class LoaderCallbacks(val context: Context) : LoaderManager.LoaderCallbacks<TaskLoaderResult<Array<Picklist>>> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskLoaderResult<Array<Picklist>>> {
            return PicklistTaskLoader(context, args)
        }
        override fun onLoaderReset(loader: Loader<TaskLoaderResult<Array<Picklist>>>) {
        }
    }
}