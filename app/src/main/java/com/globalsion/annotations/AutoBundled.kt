package com.globalsion.annotations

/**
 * Mark the variable to save to or load from the [android.os.Bundle]
 * by using the [com.globalsion.annotations.processors.AutoBundledProcessor]
 **/
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class AutoBundled(
        val name: String = "" //use default name
)