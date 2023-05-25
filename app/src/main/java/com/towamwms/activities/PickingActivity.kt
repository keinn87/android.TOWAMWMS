package com.towamwms.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.towamwms.MainApplication
import com.globalsion.dialogs.LoadingDialog
import com.towamwms.models.FetchParam
import com.globalsion.utils.ToastUtil
import com.towamwms.R
import com.towamwms.entities.Part
import com.globalsion.utils.DateUtil
import com.towamwms.databinding.ActivityPickingBinding
import com.towamwms.entities.Bin
import com.towamwms.entities.Picking
import com.towamwms.enums.EnumPickingType
import com.towamwms.models.InsertParam
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.BinApiService
import com.towamwms.services.PartApiService
import com.towamwms.taskloaders.BinTaskLoader
import com.towamwms.taskloaders.InsertPickingTaskLoader
import com.towamwms.taskloaders.PartTaskLoader
import com.towamwms.viewmodels.PickingActivityVM

class PickingActivity : AbstractTowaActivity<PickingActivityVM>() {
    companion object {
        const val INTENT_PICKING = "PICKING"
        const val STATE_PICKING = "PICKING"
        const val STATE_PICKING_TYPE = "PICKING_TYPE"
        const val LOADER_PART = 1
        const val LOADER_BIN = 2
        const val LOADER_INSERT = 3

        fun newIntent(context: Context, picking: Picking): Intent {
            val intent = Intent(context, PickingActivity::class.java)
            intent.putExtra(INTENT_PICKING, picking)
            return intent
        }
    }

    lateinit var binding: ActivityPickingBinding
    override val container: ViewGroup
        get() = binding.container

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(PickingActivityVM::class.java)
        viewModel.commandPickingTypeSelected.observe(this, Observer { pickingTypeSelected(it!!) })
        viewModel.commandLoadBarcode.observe(this, Observer { loadBarcode(it!!) })
        viewModel.commandInsertRecord.observe(this, Observer { insertRecord() })

        if (savedInstanceState == null) {
            viewModel.picking.set(intent.getParcelableExtra(INTENT_PICKING)!!)
        } else {
            viewModel.picking.set(savedInstanceState.getParcelable(STATE_PICKING)!!)
            viewModel.pickingType.set(EnumPickingType.valueOf(savedInstanceState.getInt(STATE_PICKING_TYPE)))
        }
    }

    override fun onCreateDataBinding(savedInstanceState: Bundle?) {
        super.onCreateDataBinding(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_picking)
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        val partLoader = supportLoaderManager.getLoader<TaskLoaderResult<*>>(LOADER_PART)
        val binLoader = supportLoaderManager.getLoader<TaskLoaderResult<*>>(LOADER_BIN)
        val insertLoader = supportLoaderManager.getLoader<TaskLoaderResult<*>>(LOADER_INSERT)
        val dialog = supportFragmentManager.findFragmentByTag(DIALOG_LOADING) as LoadingDialog?

        if (partLoader != null || insertLoader != null) {
            when {
                partLoader != null -> showLoadingDialog(R.string.message_loading_part)
                binLoader != null -> showLoadingDialog(R.string.message_loading_bin_pallet)
                insertLoader != null -> showLoadingDialog(R.string.message_saving)
            }
        } else {
            dialog?.dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(STATE_PICKING, viewModel.picking.get()!!)
        outState?.putInt(STATE_PICKING_TYPE, viewModel.pickingType.get()!!.value)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle(R.string.title_warning)
                .setMessage(R.string.message_cancel_confirmation)
                .setNegativeButton(R.string.text_yes) { _, _ -> finish() }
                .setPositiveButton(R.string.text_no, null)
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_trans, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_save -> insertRecord()
            R.id.menu_cancel -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun pickingTypeSelected(pickingType: EnumPickingType) {
        if (viewModel.pickingType != pickingType) {
            viewModel.barcode.set("")
            viewModel.picking.get()!!.part = null
            viewModel.picking.get()!!.originBin = null
        }

        viewModel.pickingType.set(pickingType)
        binding.editBarcode.requestFocus()
    }

    fun loadBarcode(barcode: String) {
        if (TextUtils.isEmpty(barcode)) {
            showError(R.string.message_please_input_barcode)
            return
        }

        val param = FetchParam(MainApplication.loginUser!!)
        if (viewModel.pickingType.get()!! == EnumPickingType.SINGLE) {
            param.filters[PartApiService.FILTER_BARCODE] = barcode

            supportLoaderManager.destroyLoader(LOADER_PART)
            supportLoaderManager.destroyLoader(LOADER_BIN)
            showLoadingDialog(R.string.message_loading_part)

            val args = Bundle()
            param.toBundle(args)
            supportLoaderManager.initLoader(LOADER_PART, args, partLoaderCallbacks).forceLoad()
        } else {
            param.filters[BinApiService.FILTER_CODE] = barcode

            supportLoaderManager.destroyLoader(LOADER_PART)
            supportLoaderManager.destroyLoader(LOADER_BIN)
            showLoadingDialog(R.string.message_loading_bin_pallet)

            val args = Bundle()
            param.toBundle(args)
            supportLoaderManager.initLoader(LOADER_BIN, args, binLoaderCallbacks).forceLoad()
        }
    }

    fun insertRecord() {
        if (!validateRecord()) {
            return
        }
        val param = InsertParam(MainApplication.loginUser!!, viewModel.picking.get()!!)

        supportLoaderManager.destroyLoader(LOADER_INSERT)
        showLoadingDialog(getString(R.string.message_saving))

        val bundle = Bundle()
        param.toBundle(bundle)

        supportLoaderManager.initLoader(LOADER_INSERT, bundle, insertLoaderCallbacks).forceLoad()
    }

    private fun validateRecord(): Boolean {
        var editText: EditText? = null

        var hasError = false
        val emptyPart = viewModel.pickingType.get()!! == EnumPickingType.SINGLE &&
                viewModel.picking.get()!!.part?.id ?: 0L == 0L
        val emptyBin = viewModel.pickingType.get()!! == EnumPickingType.BULK &&
                viewModel.picking.get()!!.originBin?.id ?: 0L == 0L
        if (emptyPart || emptyBin) {
            binding.editBarcode.error =
                    getText(R.string.message_please_input_barcode)
            hasError = true

            if (editText == null) {
                editText = binding.editBarcode
            }
        } else {
            binding.editBarcode.error = null
        }

        if (hasError) {
            showError(R.string.message_please_input_required_fields)

            editText?.requestFocus()
            editText?.selectAll()
        }
        return !hasError
    }

    private val partLoaderCallbacks = object : PartTaskLoader.LoaderCallbacks(this) {
        @Suppress("IntroduceWhenSubject")
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<Part>>>, data: TaskLoaderResult<Array<Part>>) {
            supportLoaderManager.destroyLoader(LOADER_PART)

            dismissLoadingDialog()
            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val part = data.value!!.singleOrNull()
            when {
                part == null -> {
                    showError(R.string.message_part_barcode_doesnt_exists)

                    binding.editBarcode.requestFocus()
                    binding.editBarcode.selectAll()
                }
                else -> {
                    viewModel.picking.get()!!.part = part
                    viewModel.commandInsertRecord.call(0)
                }
            }
        }
    }

    private val binLoaderCallbacks = object : BinTaskLoader.LoaderCallbacks(this) {
        @Suppress("IntroduceWhenSubject")
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<Bin>>>, data: TaskLoaderResult<Array<Bin>>) {
            supportLoaderManager.destroyLoader(LOADER_BIN)

            dismissLoadingDialog()
            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val bin = data.value!!.singleOrNull()
            when {
                bin == null -> {
                    showError(R.string.message_bin_pallet_doesnt_exists)

                    binding.editBarcode.requestFocus()
                    binding.editBarcode.selectAll()
                }
                else -> {
                    viewModel.picking.get()!!.originBin = bin
                    viewModel.commandInsertRecord.call(0)
                }
            }
        }
    }

    private val insertLoaderCallbacks = object : InsertPickingTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Picking>>, data: TaskLoaderResult<Picking>) {
            supportLoaderManager.destroyLoader(LOADER_INSERT)

            dismissLoadingDialog()

            val newPicking = Picking()
            newPicking.initialize()
            newPicking.documentNo = "<<Auto Number>>"
            newPicking.documentDate = DateUtil.today()
            newPicking.part = null
            newPicking.originBin = null

            if (!data.success) {
                showLoaderException(data.exception)
            } else {
                if (viewModel.pickingType.get()!! == EnumPickingType.SINGLE) {
                    val format = getString(R.string.message_picking_saved)
                    ToastUtil.show(this@PickingActivity,
                            String.format(format, data.value!!.documentNo), Toast.LENGTH_LONG)
                } else {
                    ToastUtil.show(this@PickingActivity,
                            R.string.message_picking_saved_multiple, Toast.LENGTH_LONG)
                }
                snackbar.dismiss()
            }

            viewModel.picking.set(newPicking)
            viewModel.barcode.set("")
            binding.editBarcode.requestFocus()
            binding.editBarcode.selectAll()
        }
    }
}