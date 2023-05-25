package com.globalsion.network.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class SerializedTypeName(val value: Array<String>)