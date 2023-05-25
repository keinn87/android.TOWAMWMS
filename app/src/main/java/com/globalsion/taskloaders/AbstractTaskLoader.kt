package com.globalsion.taskloaders

import android.content.Context
import android.os.Bundle
import android.support.v4.content.AsyncTaskLoader

abstract class AbstractTaskLoader<T>(context: Context, args: Bundle?) : AsyncTaskLoader<T>(context) {
    val arguments: Bundle? = args
}