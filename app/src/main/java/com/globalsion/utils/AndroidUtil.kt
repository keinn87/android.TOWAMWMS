package com.globalsion.utils

import android.content.Context
import android.content.res.Resources
import android.media.MediaPlayer
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager

object AndroidUtil {
    /**
     * Convert dp to pixel
     **/
    fun convertDpToPixel(dp: Float) : Float {
        val metrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics)
    }

    fun playSound(context: Context, uri: Uri) : MediaPlayer {
        val player = MediaPlayer.create(context, uri)
        player.setOnCompletionListener {
            it.reset()
            it.release()
        }
        player.start()
        return player
    }

    fun playSound(context: Context, resId: Int) : MediaPlayer {
        val player = MediaPlayer.create(context, resId)
        player.setOnCompletionListener {
            it.reset()
            it.release()
        }
        player.start()
        return player
    }

    fun playSoundAsset(context: Context, assetName: String) : MediaPlayer {
        val descriptor = context.assets.openFd(assetName)

        val player = MediaPlayer()
        player.setOnCompletionListener {
            it.reset()
            it.release()
        }
        player.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        descriptor.close()
        player.prepare()
        player.start()
        return player
    }

    fun hideKeyboard(view: View) {
        val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}