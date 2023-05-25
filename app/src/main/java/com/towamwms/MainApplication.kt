package com.towamwms

import android.app.Activity
import android.app.Application
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Environment
import com.towamwms.activities.SettingsActivity
import com.globalsion.utils.DateUtil
import com.towamwms.entities.User
import com.globalsion.utils.GsonUtil
import com.towamwms.activities.LoginActivity
import com.towamwms.utils.ActivityUtil
import java.io.File
import java.util.*


@Suppress("ConstantConditionIf")
class MainApplication : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        val logDirectory: String
            get() = Environment.getExternalStorageDirectory().path + "/towamwms.logs"

        var internalApplication: MainApplication? = null
        @JvmStatic
        val application: MainApplication
            get() = internalApplication!!

        val isDeveloper: Boolean
            get() = BuildConfig.DEBUG

        private var internalLoginUser: User? = null
        var loginUser: User?
            get() = internalLoginUser
            set(value) {
                if (isDeveloper) {
                    val gson = GsonUtil.gson
                    val editor = Preferences.getInstance().edit()
                    editor.putString(Preferences.STATE_LOGIN_USER, gson.toJson(value)).apply()
                }
                internalLoginUser = value
            }
    }

    init {
        internalApplication = this
    }

    override fun onCreate() {
        super.onCreate()
        logFile(logDirectory, 14)
        ActivityUtil.cacheDirectory = Environment.getExternalStorageDirectory().path + "/towamwms.cache"
        registerActivityLifecycleCallbacks(this)

        if (isDeveloper) {
            val preferences = Preferences.getInstance()
            val gson = GsonUtil.gson
            val jsonLoginUser = preferences.getString(Preferences.STATE_LOGIN_USER, "null")
            internalLoginUser = gson.fromJson(jsonLoginUser, User::class.java)
        } else {
            val editor = Preferences.getInstance().edit()
            editor.putBoolean(Preferences.API_PACKET_COMPRESSION, true).apply()
            editor.putBoolean(Preferences.API_PACKET_LOG_COMPRESSION_RATE, true).apply()
            editor.putBoolean(Preferences.API_PACKET_LOG_RAW_JSON, true).apply()

            loginUser = null
        }
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
        if (loginUser == null) {
            if (activity is SettingsActivity) {
                val needLogin = activity.intent.getBooleanExtra(SettingsActivity.INTENT_NEED_LOGIN, false)
                if (needLogin) {
                    val intent = LoginActivity.newIntent(activity)
                    activity.startActivity(intent)
                }
            } else if (activity !is LoginActivity) {
                val intent = LoginActivity.newIntent(activity!!)
                activity.startActivity(intent)
            }
        }
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    fun logFile(logDirectory: String, cleanupDays: Int) {
        try {
            val directory = File(logDirectory)
            if (!directory.exists()) {
                directory.mkdir()
            }

            val timestamp = DateUtil.format("ddMMyyyy_HHmmss", Date())
            val file = File(directory, "log_$timestamp.txt")

            Runtime.getRuntime().exec("logcat -c")
            Runtime.getRuntime().exec("logcat -f " + file.path)

            if (cleanupDays >= 1) {
                val calendar = Calendar.getInstance()
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_MONTH, -cleanupDays)

                for (f in directory.listFiles()) {
                    if (f.lastModified() < calendar.timeInMillis) {
                        try {
                            f.delete()
                        } catch (e: Exception) {
                            //ignore
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getDatabasePath(name: String?): File {
        return File(Environment.getExternalStorageDirectory().path + "/globalsion.testing/" + name)
    }

    override fun openOrCreateDatabase(name: String?, mode: Int, factory: SQLiteDatabase.CursorFactory?): SQLiteDatabase {
        val newName = getDatabasePath(name)
        return super.openOrCreateDatabase(newName.path, mode, factory)
    }

    override fun openOrCreateDatabase(name: String?, mode: Int, factory: SQLiteDatabase.CursorFactory?, errorHandler: DatabaseErrorHandler?): SQLiteDatabase {
        val newName = getDatabasePath(name)
        return super.openOrCreateDatabase(newName.path, mode, factory, errorHandler)
    }
}