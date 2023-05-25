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
import com.globalsion.utils.DateUtil
import com.globalsion.utils.ToastUtil
import com.towamwms.R
import com.towamwms.databinding.ActivityTransferringBinding
import com.towamwms.entities.Bin
import com.towamwms.entities.Part
import com.towamwms.entities.Transferring
import com.towamwms.enums.EnumTransferringType
import com.towamwms.models.FetchParam
import com.towamwms.models.InsertParam
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.BinApiService
import com.towamwms.services.PartApiService
import com.towamwms.taskloaders.BinTaskLoader
import com.towamwms.taskloaders.InsertTransferringTaskLoader
import com.towamwms.taskloaders.PartTaskLoader
import com.towamwms.viewmodels.TransferringActivityVM

class TransferringActivity : AbstractTowaActivity<TransferringActivityVM>() {
    companion object {
        const val INTENT_TRANSFERRING = "TRANSFERRING"
        const val STATE_TRANSFERRING = "TRANSFERRING"
        const val LOADER_SOURCE_PART = 1
        const val LOADER_SOURCE_BIN = 2
        const val LOADER_DESTINATION_BIN = 3
        const val LOADER_INSERT = 4

        fun newIntent(context: Context, transferring: Transferring): Intent {
            val intent = Intent(context, TransferringActivity::class.java)
            intent.putExtra(INTENT_TRANSFERRING, transferring)
            return intent
        }
    }

    lateinit var binding: ActivityTransferringBinding
    override val container: ViewGroup
        get() = binding.container

    override fun onCreateViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(TransferringActivityVM::class.java)
        viewModel.commandLoadBarcode.observe(this, Observer { loadBarcode(it!!) })
        viewModel.commandLoadDestinationBin.observe(this, Observer { loadDestinationBin(it!!) })
        viewModel.commandInsertRecord.observe(this, Observer { insertRecord() })
        viewModel.commandTransferringTypeSelected.observe(this, Observer { transferringTypeSelected(it!!) })

        if (savedInstanceState == null) {
            viewModel.transferring.set(intent.getParcelableExtra(INTENT_TRANSFERRING)!!)
        } else {
            viewModel.transferring.set(savedInstanceState.getParcelable(STATE_TRANSFERRING)!!)
        }
    }

    override fun onCreateDataBinding(savedInstanceState: Bundle?) {
        super.onCreateDataBinding(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transferring)
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()

        val sourcePartLoader = supportLoaderManager
                .getLoader<TaskLoaderResult<*>>(LOADER_SOURCE_PART)
        val sourceBinLoader = supportLoaderManager
                .getLoader<TaskLoaderResult<*>>(LOADER_SOURCE_BIN)
        val destinationBinLoader = supportLoaderManager
                .getLoader<TaskLoaderResult<*>>(LOADER_DESTINATION_BIN)
        val insertLoader = supportLoaderManager
                .getLoader<TaskLoaderResult<*>>(LOADER_INSERT)
        var dialog = supportFragmentManager
                .findFragmentByTag(DIALOG_LOADING) as LoadingDialog?

        if (sourcePartLoader == null || sourceBinLoader == null ||
                destinationBinLoader == null || insertLoader == null) {
            if (dialog == null) {
                dialog = LoadingDialog.newInstance()
                dialog.isCancelable = false

                when {
                    sourcePartLoader != null -> showLoadingDialog(R.string.message_loading_part)
                    sourceBinLoader != null -> showLoadingDialog(R.string.message_loading_pallet)
                    destinationBinLoader != null -> showLoadingDialog(R.string.message_loading_destination_bin_pallet)
                    insertLoader != null -> showLoadingDialog(R.string.message_saving)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(STATE_TRANSFERRING, viewModel.transferring.get())
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

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle(R.string.title_warning)
                .setMessage(R.string.message_cancel_confirmation)
                .setNegativeButton(R.string.text_yes) { _, _ -> finish() }
                .setPositiveButton(R.string.text_no, null)
                .show()
    }

    fun transferringTypeSelected(transferringType: EnumTransferringType) {
        val transferring = viewModel.transferring.get()!!
        if (transferring.type != transferringType) {
            //reset due to changing transferring type
            transferring.sourcePart = null
            transferring.sourceBin = null
            transferring.destinationBin = null

            viewModel.barcode.set("")
            viewModel.destinationBinCode.set("")
        }

        transferring.type = transferringType
        viewModel.transferring.notifyChange()
        binding.editBarcode.requestFocus()
    }

    fun loadBarcode(barcode: String) {
        if (TextUtils.isEmpty(barcode)) {
            showError(R.string.message_please_input_barcode)

            viewModel.transferring.get()!!.sourcePart = null
            viewModel.transferring.get()!!.sourceBin = null
            //viewModel.transferring.get()!!.type = EnumTransferringType.SINGLE
            viewModel.transferring.notifyChange()
            return
        }

        val param = FetchParam(MainApplication.loginUser!!)

        if (viewModel.transferring.get()!!.type == EnumTransferringType.BULK) {
            param.filters[BinApiService.FILTER_CODE] = barcode
            //param.filters[BinApiService.FILTER_TYPE] = EnumBinType.PALLET

            val sourceBinLoader = supportLoaderManager
                    .getLoader<TaskLoaderResult<*>>(LOADER_SOURCE_BIN)
            if (sourceBinLoader != null) {
                supportLoaderManager.destroyLoader(LOADER_SOURCE_BIN)
            }

            showLoadingDialog(R.string.message_loading_bin_pallet)

            val bundle = Bundle()
            param.toBundle(bundle)

            supportLoaderManager.initLoader(LOADER_SOURCE_BIN,
                    bundle, sourceBinLoaderCallbacks).forceLoad()
        } else {
            param.filters[PartApiService.FILTER_BARCODE] = barcode

            supportLoaderManager.destroyLoader(LOADER_SOURCE_PART)
            showLoadingDialog(R.string.message_loading_part)

            val bundle = Bundle()
            param.toBundle(bundle)

            supportLoaderManager.initLoader(LOADER_SOURCE_PART,
                    bundle, sourcePartLoaderCallbacks).forceLoad()
        }
    }

    fun loadDestinationBin(destinationBinCode: String) {
        if (TextUtils.isEmpty(destinationBinCode)) {
            showError(R.string.message_please_input_destination_bin_code)

            viewModel.transferring.get()!!.destinationBin = null
            viewModel.transferring.notifyChange()
            return
        }

        supportLoaderManager.destroyLoader(LOADER_DESTINATION_BIN)
        showLoadingDialog(R.string.message_loading_destination_bin_pallet)

        val param = FetchParam(MainApplication.loginUser!!)
        param.filters[BinApiService.FILTER_CODE] = destinationBinCode

        val bundle = Bundle()
        param.toBundle(bundle)

        supportLoaderManager.initLoader(LOADER_DESTINATION_BIN,
                bundle, destinationBinCallbacks).forceLoad()
    }

    fun insertRecord() {
        if (!validateRecord()) {
            return
        }
        val param = InsertParam(MainApplication.loginUser!!, viewModel.transferring.get()!!)

        supportLoaderManager.destroyLoader(LOADER_INSERT)
        showLoadingDialog(R.string.message_saving)

        val args = Bundle()
        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_INSERT, args, insertLoaderCallbacks).forceLoad()
    }

    private fun validateRecord(): Boolean {
        var editText: EditText? = null
        var hasError = false

        if (viewModel.transferring.get()!!.sourcePart == null &&
                viewModel.transferring.get()!!.sourceBin == null) {
            binding.editBarcode.error =
                    getString(R.string.message_please_input_barcode)
            hasError = true

            if (editText == null) {
                editText = binding.editBarcode
            }
        } else {
            binding.editBarcode.error = null
        }
        if (viewModel.transferring.get()!!.destinationBin == null) {
            binding.editDestinationBinCode.error =
                    getString(R.string.message_please_input_destination_bin_code)
            hasError = true

            if (editText == null) {
                editText = binding.editDestinationBinCode
            }
        } else {
            binding.editDestinationBinCode.error = null
        }

        if (hasError) {
            showError(R.string.message_please_input_required_fields)

            editText?.requestFocus()
            editText?.selectAll()
        }
        return !hasError
    }

    private val sourcePartLoaderCallbacks = object : PartTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<Part>>>, data: TaskLoaderResult<Array<Part>>) {
            supportLoaderManager.destroyLoader(LOADER_SOURCE_PART)

            dismissLoadingDialog()

            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val part = data.value!!.singleOrNull()
            when {
                part == null -> {
                    showError(R.string.message_part_barcode_doesnt_exists)

                    val partBarcode = viewModel.transferring.get()!!.sourcePart?.barcode
                    val binCode = viewModel.transferring.get()!!.sourceBin?.code
                    viewModel.barcode.set(partBarcode ?: binCode ?: "")
                    binding.editBarcode.requestFocus()
                    binding.editBarcode.selectAll()
                }
                part.bin == null -> {
                    showError(R.string.message_part_not_received)

                    val partBarcode = viewModel.transferring.get()!!.sourcePart?.barcode
                    val binCode = viewModel.transferring.get()!!.sourceBin?.code
                    viewModel.barcode.set(partBarcode ?: binCode ?: "")
                    binding.editBarcode.requestFocus()
                    binding.editBarcode.selectAll()
                }
                else -> {
                    viewModel.barcode.set(part.barcode)
                    viewModel.transferring.get()!!.sourcePart = part
                    viewModel.transferring.get()!!.sourceBin = null
                    //viewModel.transferring.get()!!.type = EnumTransferringType.SINGLE
                    viewModel.transferring.notifyChange()
                    snackbar.dismiss()
                    binding.editBarcode.error = null
                    binding.editDestinationBinCode.requestFocus()
                    binding.editDestinationBinCode.selectAll()
                }
            }
        }
    }

    @Suppress("IntroduceWhenSubject")
    private val sourceBinLoaderCallbacks = object : BinTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<Bin>>>, data: TaskLoaderResult<Array<Bin>>) {
            supportLoaderManager.destroyLoader(LOADER_SOURCE_BIN)

            dismissLoadingDialog()

            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val bin = data.value!!.singleOrNull()
            when {
                bin == null -> {
                    showError(R.string.message_invalid_bin_pallet)

                    val partBarcode = viewModel.transferring.get()!!.sourcePart?.barcode
                    val binCode = viewModel.transferring.get()!!.sourceBin?.code
                    viewModel.barcode.set(partBarcode ?: binCode ?: "")
                    binding.editBarcode.requestFocus()
                    binding.editBarcode.selectAll()
                }
                else -> {
                    viewModel.barcode.set(bin.code)
                    viewModel.transferring.get()!!.sourcePart = null
                    viewModel.transferring.get()!!.sourceBin = bin
                    //viewModel.transferring.get()!!.type = EnumTransferringType.BULK
                    viewModel.transferring.notifyChange()
                    snackbar.dismiss()
                    binding.editBarcode.error = null
                    binding.editDestinationBinCode.requestFocus()
                    binding.editDestinationBinCode.selectAll()
                }
            }
        }
    }

    @Suppress("IntroduceWhenSubject")
    private val destinationBinCallbacks = object : BinTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<Bin>>>, data: TaskLoaderResult<Array<Bin>>) {
            supportLoaderManager.destroyLoader(LOADER_DESTINATION_BIN)

            dismissLoadingDialog()

            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val bin = data.value!!.singleOrNull()
            when {
                bin == null -> {
                    showError(R.string.message_invalid_destination_bin_pallet)

                    viewModel.destinationBinCode.set(viewModel
                            .transferring.get()!!.destinationBin?.code ?: "")
                    binding.editDestinationBinCode.requestFocus()
                    binding.editDestinationBinCode.selectAll()
                }
                else -> {
                    viewModel.destinationBinCode.set(bin.code)
                    viewModel.transferring.get()!!.destinationBin = bin
                    viewModel.transferring.notifyChange()
                    binding.editDestinationBinCode.error = null
                    viewModel.commandInsertRecord.call(0)
                }
            }
        }
    }

    private val insertLoaderCallbacks = object : InsertTransferringTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Transferring>>, data: TaskLoaderResult<Transferring>) {
            supportLoaderManager.destroyLoader(LOADER_INSERT)

            dismissLoadingDialog()

            val newTransferring = Transferring()
            newTransferring.initialize()
            newTransferring.type = viewModel.transferring.get()!!.type
            newTransferring.documentNo = "<<Auto Number>>"
            newTransferring.documentDate = DateUtil.today()
            newTransferring.destinationBin = null
            newTransferring.sourceBin = null
            newTransferring.sourcePart = null

            if (!data.success) {
                showLoaderException(data.exception)
            } else {
                val format = getText(R.string.message_transferring_saved).toString()
                ToastUtil.show(this@TransferringActivity, String.format(format, data.value!!.documentNo), Toast.LENGTH_LONG)

                snackbar.dismiss()
            }
            viewModel.transferring.set(newTransferring)

            viewModel.barcode.set("")
            viewModel.destinationBinCode.set("")
            binding.editBarcode.requestFocus()
            binding.editBarcode.selectAll()
        }
    }
}