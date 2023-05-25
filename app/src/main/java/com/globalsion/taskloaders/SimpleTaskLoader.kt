package com.globalsion.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader

class SimpleTaskLoader<T>(context: Context, args: Bundle?) : AbstractTaskLoader<T>(context, args) {

    private lateinit var loadInBackgroundCallback: LoadInBackgroundCallback<T>

    fun setLoadInBackgroundCallback(callback: LoadInBackgroundCallback<T>) {
        loadInBackgroundCallback = callback
    }
    fun setLoadInBackgroundCallback(callback: (loader: SimpleTaskLoader<T>) -> T?) {
        loadInBackgroundCallback = object: LoadInBackgroundCallback<T> {
            override fun onLoadInBackground(loader: SimpleTaskLoader<T>): T? {
                return callback(loader)
            }
        }
    }

    override fun loadInBackground(): T? {
        return loadInBackgroundCallback.onLoadInBackground(this)
    }

    interface LoadInBackgroundCallback<T> {
        fun onLoadInBackground(loader: SimpleTaskLoader<T>) : T?
    }

    abstract class LoaderCallbacks<T>(val context: Context) : LoaderManager.LoaderCallbacks<T> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<T> {
            return SimpleTaskLoader<T>(context, args)
        }
        override fun onLoaderReset(loader: Loader<T>) {
        }
    }
}