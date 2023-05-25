package com.globalsion.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.RequiresPermission
import com.towamwms.MainApplication

/**
 * Vibrator
 */
object VibrateUtil {

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun vibrate(millis: Long) {
        val vibrator = MainApplication.application
                .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(millis)
        }
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    @TargetApi(Build.VERSION_CODES.O)
    fun vibrate(effect: VibrationEffect) {
        val vibrator = MainApplication.application
                .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(effect)
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun cancel() {
        val vibrator = MainApplication.application
                .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
    }
}