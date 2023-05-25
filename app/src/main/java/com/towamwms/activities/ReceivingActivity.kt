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
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.towamwms.MainApplication
import com.globalsion.dialogs.LoadingDialog
import com.globalsion.utils.AndroidUtil
import com.globalsion.utils.DateUtil
import com.globalsion.utils.ToastUtil
import com.towamwms.Assets
import com.towamwms.R
import com.towamwms.databinding.ActivityReceivingBinding
import com.towamwms.entities.Bin
import com.towamwms.entities.Part
import com.towamwms.entities.Receiving
import com.towamwms.enums.EnumBinGroup
import com.towamwms.models.FetchParam
import com.towamwms.models.InsertParam
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.BinApiService
import com.towamwms.services.PartApiService
import com.towamwms.taskloaders.BinTaskLoader
import com.towamwms.taskloaders.InsertReceivingTaskLoader
import com.towamwms.taskloaders.PartTaskLoader
import com.towamwms.viewmodels.ReceivingActivityVM

class ReceivingActivity : AbstractTowaActivity<ReceivingActivityVM>() {
    companion object {
        const val INTENT_RECEIVING = "RECEIVING"
        const val STATE_RECEIVING = "RECEIVING"
        const val LOADER_PART = 1
        const val LOADER_BIN = 2
        const val LOADER_INSERT = 3

        fun newIntent(context: Context, receiving: Receiving): Intent {
            val intent = Intent(context, ReceivingActivity::class.java)
            intent.putExtra(INTENT_RECEIVING, receiving)
            return intent
        }
    }

    lateinit var binding: ActivityReceivingBinding
    override val container: ViewGroup
        get() = binding.container

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ReceivingActivityVM::class.java)
        viewModel.commandLoadPart.observe(this, Observer { loadPart(it!!) })
        viewModel.commandLoadBin.observe(this, Observer { loadBin(it!!) })
        viewModel.commandInsertRecord.observe(this, Observer { insertRecord() })

        if (savedInstanceState == null) {
            viewModel.receiving.set(intent.getParcelableExtra(INTENT_RECEIVING)!!)
        } else {
            viewModel.receiving.set(savedInstanceState.getParcelable(STATE_RECEIVING)!!)
        }
    }

    override fun onCreateDataBinding(savedInstanceState: Bundle?) {
        super.onCreateDataBinding(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receiving)
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

        if (partLoader != null || binLoader != null || insertLoader != null) {
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
        outState?.putParcelable(STATE_RECEIVING, viewModel.receiving.get())
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

    fun loadPart(partBarcode: String) {
        if (TextUtils.isEmpty(partBarcode)) {
            showError(R.string.message_please_input_part_barcode)

            viewModel.receiving.get()!!.part = null
            viewModel.receiving.notifyChange()
            return
        }

        val param = FetchParam(MainApplication.loginUser!!)
        param.filters[PartApiService.FILTER_BARCODE] = partBarcode

        supportLoaderManager.destroyLoader(LOADER_PART)
        showLoadingDialog(getString(R.string.message_loading_part))

        val bundle = Bundle()
        param.toBundle(bundle)

        supportLoaderManager.initLoader(LOADER_PART, bundle, partLoaderCallbacks).forceLoad()
    }

    fun loadBin(binCode: String) {
        if (TextUtils.isEmpty(binCode)) {
            showError(R.string.message_please_input_bin)

            viewModel.receiving.get()!!.bin = null
            viewModel.receiving.notifyChange()
            return
        }

        val param = FetchParam(MainApplication.loginUser!!)
        param.filters[BinApiService.FILTER_CODE] = binCode

        supportLoaderManager.destroyLoader(LOADER_BIN)
        showLoadingDialog(getString(R.string.message_loading_bin_pallet))

        val bundle = Bundle()
        param.toBundle(bundle)

        supportLoaderManager.initLoader(LOADER_BIN, bundle, binLoaderCallbacks).forceLoad()
    }

    fun insertRecord() {
        if (!validateRecord()) {
            return
        }
        val param = InsertParam(MainApplication.loginUser!!, viewModel.receiving.get()!!)

        supportLoaderManager.destroyLoader(LOADER_INSERT)
        showLoadingDialog(getString(R.string.message_saving))

        val bundle = Bundle()
        param.toBundle(bundle)

        supportLoaderManager.initLoader(LOADER_INSERT, bundle, insertLoaderCallbacks).forceLoad()
    }

    private fun validateRecord(): Boolean {
        var editText: EditText? = null

        var hasError = false
        if (viewModel.receiving.get()!!.part?.id ?: 0L == 0L) {
            binding.editPartBarcode.error =
                    getText(R.string.message_please_input_part_barcode)
            hasError = true

            if (editText == null) {
                editText = binding.editPartBarcode
            }
        } else {
            binding.editPartBarcode.error = null
        }
        if (viewModel.receiving.get()!!.bin?.id ?: 0L == 0L) {
            binding.editBin.error =
                    getText(R.string.message_please_input_bin)
            hasError = true

            if (editText == null) {
                editText = binding.editBin
            }
        } else {
            binding.editBin.error = null
        }

        if (hasError) {
            showError(R.string.message_please_input_required_fields)

            editText?.requestFocus()
            editText?.selectAll()
        }
        return !hasError
    }

    private val partLoaderCallbacks = object : PartTaskLoader.LoaderCallbacks(this) {
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

                    viewModel.partBarcode.set(viewModel.receiving.get()!!.part?.barcode ?: "")
                    binding.editPartBarcode.requestFocus()
                    binding.editPartBarcode.selectAll()
                }
                part.bin != null -> {
                    showError(R.string.message_part_already_received)

                    viewModel.partBarcode.set(viewModel.receiving.get()!!.part?.barcode ?: "")
                    binding.editPartBarcode.requestFocus()
                    binding.editPartBarcode.selectAll()
                }
                else -> {
                    viewModel.partBarcode.set(part.barcode)
                    viewModel.receiving.get()!!.part = part
                    viewModel.receiving.notifyChange()
                    snackbar.dismiss()
                    binding.editPartBarcode.error = null
                    binding.editBin.requestFocus()
                    binding.editBin.selectAll()

                    if (!TextUtils.isEmpty(viewModel.binCode.get())) {
                        viewModel.onEditorAction(binding.editBin,
                                EditorInfo.IME_ACTION_UNSPECIFIED,
                                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                    }
                }
            }
        }
    }

    private val binLoaderCallbacks = object : BinTaskLoader.LoaderCallbacks(this) {
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

                    viewModel.binCode.set(viewModel.receiving.get()!!.bin?.code ?: "")
                    binding.editBin.requestFocus()
                    binding.editBin.selectAll()
                }
                bin.group != EnumBinGroup.STAGING &&
                        bin.group != EnumBinGroup.QUALITYCONTROL -> {
                    showError(R.string.message_bin_group_only_staging_or_quality_control)

                    viewModel.binCode.set(viewModel.receiving.get()!!.bin?.code ?: "")
                    binding.editBin.requestFocus()
                    binding.editBin.selectAll()
                }
                else -> {
                    viewModel.binCode.set(bin.code)
                    viewModel.receiving.get()!!.bin = bin
                    viewModel.receiving.notifyChange()

                    binding.editBin.error = null
                    viewModel.commandInsertRecord.call(0)
                }
            }
        }
    }

    private val insertLoaderCallbacks = object : InsertReceivingTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Receiving>>, data: TaskLoaderResult<Receiving>) {
            supportLoaderManager.destroyLoader(LOADER_INSERT)

            dismissLoadingDialog()

            val newReceiving = Receiving()
            newReceiving.initialize()
            newReceiving.documentNo = "<<Auto Number>>"
            newReceiving.documentDate = DateUtil.today()
            newReceiving.part = null
            newReceiving.bin = null

            if (!data.success) {
                //special handled error!
                if (data.exception?.message == "QC_ERROR") {
                    showError(R.string.message_quality_control_required, Assets.QC_ERROR)


                    viewModel.binCode.set(viewModel.receiving.get()!!.bin?.code ?: "")
                    viewModel.receiving.get()!!.bin = null
                    viewModel.receiving.notifyChange()
                    binding.editBin.requestFocus()
                    binding.editBin.selectAll()
                    return
                } else {
                    showLoaderException(data.exception)
                }
            } else {
                val format = getText(R.string.message_receiving_saved).toString()
                ToastUtil.show(this@ReceivingActivity,
                        String.format(format, data.value!!.documentNo), Toast.LENGTH_LONG)

                snackbar.dismiss()
            }

            viewModel.receiving.set(newReceiving)
            viewModel.partBarcode.set("")
            binding.editPartBarcode.requestFocus()
            binding.editPartBarcode.selectAll()
        }
    }
}