package com.towamwms.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.towamwms.MainApplication
import com.globalsion.activities.AbstractVMActivity
import com.globalsion.utils.DateUtil
import com.towamwms.R
import com.towamwms.databinding.ActivityMainBinding
import com.towamwms.entities.Issuing
import com.towamwms.entities.Picking
import com.towamwms.entities.Receiving
import com.towamwms.entities.Transferring
import com.towamwms.enums.EnumTransferringType
import com.towamwms.viewmodels.MainActivityVM

class MainActivity: AbstractVMActivity<MainActivityVM>() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    lateinit var binding: ActivityMainBinding

    override fun onCreateViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(MainActivityVM::class.java)
        viewModel.commandButton.observe(this, Observer { commandButton(it!!) })
    }

    override fun onCreateDataBinding(savedInstanceState: Bundle?) {
        super.onCreateDataBinding(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar as Toolbar)

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        title = title.toString() + " ($versionName)"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_logout -> {
                MainApplication.loginUser = null
                val intent = LoginActivity.newIntent(this)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun commandButton(@IdRes viewId: Int) {
        when (viewId) {
            R.id.button_receiving -> {
                val receiving = Receiving()
                receiving.initialize()
                receiving.documentNo = "<<Auto Number>>"
                receiving.documentDate = DateUtil.today()
                receiving.part = null
                receiving.bin = null

                val intent = ReceivingActivity.newIntent(this, receiving)
                startActivity(intent)
            }
            R.id.button_transferring -> {
                val transferring = Transferring()
                transferring.initialize()
                transferring.type = EnumTransferringType.BULK
                transferring.documentNo = "<<Auto Number>>"
                transferring.documentDate = DateUtil.today()
                transferring.destinationBin = null
                transferring.sourcePart = null
                transferring.sourceBin = null

                val intent = TransferringActivity.newIntent(this, transferring)
                startActivity(intent)
            }
            R.id.button_picking -> {
                val picking = Picking()
                picking.initialize()
                picking.documentNo = "<<Auto Number>>"
                picking.documentDate = DateUtil.today()
                picking.part = null
                picking.originBin = null

                val intent = PickingActivity.newIntent(this, picking)
                startActivity(intent)
            }
            R.id.button_issuing -> {
                val issuing = Issuing()
                issuing.initialize()
                issuing.documentNo = "<<Auto Number>>"
                issuing.documentDate = DateUtil.today()
                issuing.userSigned = null

                val intent = IssuingActivity.newIntent(this, issuing)
                startActivity(intent)
            }
            R.id.button_final_barcode_printing -> {
                val intent = FinalBarcodePrintingActivity.newIntent(this)
                startActivity(intent)
            }
            R.id.button_part_enquiry-> {
                val intent = PartEnquiryActivity.newIntent(this)
                startActivity(intent)
            }
            R.id.button_settings -> {
                val intent = SettingsActivity.newIntent(this, true)
                startActivity(intent)
            }
        }
    }
}