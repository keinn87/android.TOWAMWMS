package com.towamwms.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.Loader
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import com.towamwms.MainApplication
import com.towamwms.Preferences
import com.globalsion.dialogs.LoadingDialog
import com.towamwms.models.FetchParam
import com.towamwms.models.PrintParam
import com.globalsion.utils.ToastUtil
import com.towamwms.R
import com.towamwms.databinding.ActivityFinalBarcodePrintingBinding
import com.towamwms.entities.Part
import com.towamwms.enums.EnumReport
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.PartApiService
import com.towamwms.taskloaders.PartTaskLoader
import com.towamwms.taskloaders.PrintTaskLoader
import com.towamwms.viewmodels.FinalBarcodePrintingActivityVM

class FinalBarcodePrintingActivity : AbstractTowaActivity<FinalBarcodePrintingActivityVM>() {
    companion object {
        const val LOADER_PART = 1
        const val LOADER_PRINT = 2

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, FinalBarcodePrintingActivity::class.java)
            return intent
        }
    }

    lateinit var binding: ActivityFinalBarcodePrintingBinding
    override val container: ViewGroup
        get() = binding.container

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(FinalBarcodePrintingActivityVM::class.java)
        viewModel.commandLoadPart.observe(this, Observer { loadPart(it!!) })
        viewModel.commandPrint.observe(this, Observer { print(it!!) })
    }

    override fun onCreateDataBinding(savedInstanceState: Bundle?) {
        super.onCreateDataBinding(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_final_barcode_printing)
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
        val printLoader = supportLoaderManager.getLoader<TaskLoaderResult<*>>(LOADER_PRINT)
        val dialog = supportFragmentManager.findFragmentByTag(DIALOG_LOADING) as LoadingDialog?

        if (partLoader != null || printLoader != null) {
            when {
                partLoader != null -> showLoadingDialog(R.string.message_loading_part)
                printLoader != null -> showLoadingDialog(R.string.message_printing)
            }
        } else {
            dialog?.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    fun loadPart(partBarcode: String) {
        if (TextUtils.isEmpty(partBarcode)) {
            showError(R.string.message_please_input_part_barcode)
            return
        }


        val param = FetchParam(MainApplication.loginUser!!)
        param.filters[PartApiService.FILTER_BARCODE] = partBarcode

        supportLoaderManager.destroyLoader(LOADER_PART)
        showLoadingDialog(getString(R.string.message_loading_part))

        val args = Bundle()
        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_PART, args, partLoaderCallbacks).forceLoad()
    }

    fun print(partIds: Array<Long>) {
        val param = PrintParam(MainApplication.loginUser!!, EnumReport.LABEL_BARCODE_FINAL)
        param.parameters["part_ids"] = partIds
        param.printer = Preferences.getInstance()
                .getString(Preferences.PRINTER_NAME, null)

        if (TextUtils.isEmpty(param.printer)) {
            showError(R.string.message_please_setup_printer_name)
            return
        }

        supportLoaderManager.destroyLoader(LOADER_PRINT)
        showLoadingDialog(getString(R.string.message_printing))

        val args = Bundle()
        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_PRINT, args, printLoaderCallbacks).forceLoad()
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

                    binding.editPartBarcode.requestFocus()
                    binding.editPartBarcode.selectAll()
                }
                else -> {
                    viewModel.commandPrint.call(arrayOf(part.id))
                }
            }
        }
    }

    private val printLoaderCallbacks = object : PrintTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Boolean>>, data: TaskLoaderResult<Boolean>) {
            supportLoaderManager.destroyLoader(LOADER_PRINT)

            dismissLoadingDialog()
            if (!data.success) {
                showLoaderException(data.exception)
            } else {
                ToastUtil.show(this@FinalBarcodePrintingActivity,
                        R.string.message_print_triggered_successfully, Toast.LENGTH_SHORT)
                snackbar.dismiss()
            }
            viewModel.partBarcode.set("")
            binding.editPartBarcode.requestFocus()
            binding.editPartBarcode.selectAll()
        }
    }
}