package com.towamwms.models

data class TaskLoaderResult<T>(
    var success: Boolean,
    var value: T?,
    var exception: Exception?
)