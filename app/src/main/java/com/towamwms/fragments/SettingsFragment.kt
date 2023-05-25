package com.towamwms.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v14.preference.PreferenceFragment
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceCategory
import android.text.TextUtils
import android.webkit.URLUtil
import android.widget.Toast
import com.towamwms.Preferences
import com.towamwms.MainApplication
import com.towamwms.R
import com.globalsion.utils.ToastUtil

class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {
    companion object {
        private const val CATEGORY_SYSTEM_SETTINGS = "CATEGORY_SYSTEM_SETTINGS"
        private const val CATEGORY_DEVELOPER_SETTINGS = "CATEGORY_DEVELOPER_SETTINGS"

        fun newInstance() : SettingsFragment {
            return SettingsFragment()
        }
    }

    private var categorySystemSettings: PreferenceCategory? = null
    private var categoryDeveloperSettings: PreferenceCategory? = null
    private var preferenceApiServerURL: EditTextPreference? = null
    private var preferenceApiPacketCompression: SwitchPreference? = null
    private var preferenceApiPacketLogCompressionRate: SwitchPreference? = null
    private var preferenceApiPacketLogRawJSON: SwitchPreference? = null
    private var preferencePrinterName: EditTextPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        categorySystemSettings = findPreference(CATEGORY_SYSTEM_SETTINGS) as PreferenceCategory?
        categoryDeveloperSettings = findPreference(CATEGORY_DEVELOPER_SETTINGS) as PreferenceCategory?
        preferenceApiServerURL = findPreference(Preferences.API_SERVER_URL) as EditTextPreference?
        preferenceApiPacketCompression = findPreference(Preferences.API_PACKET_COMPRESSION) as SwitchPreference?
        preferenceApiPacketLogCompressionRate = findPreference(Preferences.API_PACKET_LOG_COMPRESSION_RATE) as SwitchPreference?
        preferenceApiPacketLogRawJSON = findPreference(Preferences.API_PACKET_LOG_RAW_JSON) as SwitchPreference?
        preferencePrinterName = findPreference(Preferences.PRINTER_NAME) as EditTextPreference?

        @Suppress("ConstantConditionIf")
        if (!MainApplication.isDeveloper) {
            categoryDeveloperSettings!!.isVisible = false
            preferenceApiPacketCompression!!.isVisible = false
            preferenceApiPacketLogCompressionRate!!.isVisible = false
            preferenceApiPacketLogRawJSON!!.isVisible = false
        }

        preferenceApiServerURL!!.dialogLayoutResource = R.layout.preference_dialog_uri_edit_text

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val restURL = preferences.getString(Preferences.API_SERVER_URL, null)
        val restCompression = preferences.getBoolean(Preferences.API_PACKET_COMPRESSION, false)
        val restLogCompressionRate = preferences.getBoolean(Preferences.API_PACKET_LOG_COMPRESSION_RATE, false)
        val restLogRawJSON = preferences.getBoolean(Preferences.API_PACKET_LOG_RAW_JSON, false)
        val printerName = preferences.getString(Preferences.PRINTER_NAME, null)

        if (TextUtils.isEmpty(restURL)) {
            preferenceApiServerURL!!.summary = getText(R.string.summary_server_address)
        } else {
            preferenceApiServerURL!!.summary = restURL
        }
        if (TextUtils.isEmpty(printerName)) {
            preferencePrinterName!!.summary = getText(R.string.summary_printer_name)
        } else {
            preferencePrinterName!!.summary = printerName
        }
        if (restCompression) {
            preferenceApiPacketCompression!!.summary = getText(R.string.summary_enabled)
        } else {
            preferenceApiPacketCompression!!.summary = getText(R.string.summary_disabled)
        }
        if (restLogCompressionRate) {
            preferenceApiPacketLogCompressionRate!!.summary = getText(R.string.summary_enabled)
        } else {
            preferenceApiPacketLogCompressionRate!!.summary = getText(R.string.summary_disabled)
        }
        if (restLogRawJSON) {
            preferenceApiPacketLogRawJSON!!.summary = getText(R.string.summary_enabled)
        } else {
            preferenceApiPacketLogRawJSON!!.summary = getText(R.string.summary_disabled)
        }
        preferenceApiServerURL!!.onPreferenceChangeListener = this
        preferenceApiPacketCompression!!.onPreferenceChangeListener = this
        preferenceApiPacketLogCompressionRate!!.onPreferenceChangeListener = this
        preferenceApiPacketLogRawJSON!!.onPreferenceChangeListener = this
        preferencePrinterName!!.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        if (preference == preferenceApiServerURL) {
            val restURL = newValue as String?

            if (TextUtils.isEmpty(restURL)) {
                ToastUtil.show(activity, R.string.message_invalid_rest_url, Toast.LENGTH_LONG)
                return false
            } else if (!URLUtil.isHttpUrl(restURL) && !URLUtil.isHttpsUrl(restURL)) {
                ToastUtil.show(activity, R.string.message_invalid_rest_url_2, Toast.LENGTH_LONG)
                return false
            } else {
                preference.summary = restURL
            }
        } else if (preference == preferencePrinterName) {
            val printerName = newValue as String?

            if (TextUtils.isEmpty(printerName)) {
                preference.summary = getText(R.string.summary_printer_name)
            } else {
                preference.summary = printerName
            }
        } else if (preference == preferenceApiPacketCompression ||
                preference == preferenceApiPacketLogCompressionRate ||
                preference == preferenceApiPacketLogRawJSON) {
            val enabled = newValue as Boolean

            if (enabled) {
                preference.summary = getText(R.string.summary_enabled)
            } else {
                preference.summary = getText(R.string.summary_disabled)
            }
        }

        return true
    }
}
