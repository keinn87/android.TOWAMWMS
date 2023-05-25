package com.globalsion.annotations

/**
 * Experimental.
 * Mark the variable to be auto processed and will not perform callback.
 * Only can use on types implemented parcelable and serializable..
 * @see com.globalsion.helpers.ParcelHelper.save
 * @see com.globalsion.helpers.ParcelHelper.load
 **/
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class AutoParceled