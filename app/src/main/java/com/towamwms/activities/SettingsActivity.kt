package com.towamwms.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.globalsion.activities.AbstractActivity
import com.towamwms.fragments.SettingsFragment
import com.towamwms.R

open class SettingsActivity : AbstractActivity() {
    companion object {
        const val INTENT_NEED_LOGIN = "BYPASS_LOGIN"
        private const val FRAG_SETTINGS = "FRAG_SETTINGS"

        fun newIntent(context: Context, needLogin: Boolean): Intent {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(INTENT_NEED_LOGIN, needLogin)
            return intent
        }
    }

    var toolbar: Toolbar? = null
    var settingsFragment: SettingsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        settingsFragment = supportFragmentManager
                .findFragmentByTag(FRAG_SETTINGS) as SettingsFragment?

        if (settingsFragment == null) {
            settingsFragment = onCreateSettingsFragment()
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, settingsFragment, FRAG_SETTINGS)
                    .commit()
        }
    }

    open fun onCreateSettingsFragment() : SettingsFragment {
        return SettingsFragment.newInstance()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
}