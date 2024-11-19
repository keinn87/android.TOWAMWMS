package com.towamwms.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.Loader
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.view.ViewGroup
import com.globalsion.dialogs.DropDownDialog
import com.globalsion.dialogs.LoadingDialog
import com.towamwms.MainApplication
import com.towamwms.R
import com.towamwms.adapter.DistributionReadyBinDialogAdapter
import com.towamwms.adapter.JobSummaryViewDialogAdapter
import com.towamwms.databinding.ActivityPartEnquiryBinding
import com.towamwms.entities.JobSummaryView
import com.towamwms.entities.Part
import com.towamwms.models.FetchParam
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.JobSummaryViewApiService
import com.towamwms.services.PartApiService
import com.towamwms.taskloaders.DistributionReadyBinTaskLoader
import com.towamwms.taskloaders.JobSummaryViewTaskLoader
import com.towamwms.taskloaders.PartTaskLoader
import com.towamwms.viewmodels.PartEnquiryActivityVM

class PartEnquiryActivity : AbstractTowaActivity<PartEnquiryActivityVM>() {
    companion object {
        const val DIALOG_DETAILS = "DIALOG_DETAILS"
        const val LOADER_PART = 1
        const val LOADER_MFG_NO = 2
        const val LOADER_BIN_NO = 3

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, PartEnquiryActivity::class.java)
            return intent
        }
    }

    lateinit var binding: ActivityPartEnquiryBinding
    override val container: ViewGroup
        get() = binding.container

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(PartEnquiryActivityVM::class.java)
        viewModel.commandLoadPart.observe(this, Observer { loadPart(it!!) })
        viewModel.commandLoadJobSummaryView.observe(this, Observer { loadJobSummaryView() })
        viewModel.commandLoadDistributionReadyBin.observe(this, Observer { loadDistributionReadyBins() })
    }

    override fun onCreateDataBinding(savedInstanceState: Bundle?) {
        super.onCreateDataBinding(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_part_enquiry)
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
        val dialog = supportFragmentManager.findFragmentByTag(DIALOG_LOADING) as LoadingDialog?

        if (partLoader != null) {
            when {
                partLoader != null -> showLoadingDialog(R.string.message_loading_part)
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
        param.filters[PartApiService.FILTER_DELETED] = false

        supportLoaderManager.destroyLoader(LOADER_PART)
        showLoadingDialog(getString(R.string.message_loading_part))

        val args = Bundle()
        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_PART, args, partLoaderCallbacks).forceLoad()
    }

    fun loadDistributionReadyBins() {
        if (TextUtils.isEmpty(viewModel.part.get()?.barcode)) {
            showError(R.string.message_please_input_part_barcode)
            return
        }

        val param = FetchParam(MainApplication.loginUser!!)
        param.filters[PartApiService.FILTER_MFGNO] = viewModel.part.get()!!.mfgNo

        supportLoaderManager.destroyLoader(LOADER_PART)
        showLoadingDialog(getString(R.string.message_loading_part))

        val args = Bundle()
        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_PART, args, distributionReadyBinsLoaderCallbacks).forceLoad()
    }

    fun loadJobSummaryView() {

        if (viewModel.part == null) {
            showError(R.string.message_please_input_part_barcode)
            return
        }

        val param = FetchParam(MainApplication.loginUser!!)
        param.filters[JobSummaryViewApiService.FILTER_MFG_NO] = viewModel.part.get()!!.mfgNo


        supportLoaderManager.destroyLoader(LOADER_MFG_NO)
        showLoadingDialog("Loading Summary Mfg No...")

        val args = Bundle()
        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_MFG_NO, args, jobSummaryViewLoaderCallbacks).forceLoad()

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
                    viewModel.part.set(part)

                }
            }
        }
    }

    private val distributionReadyBinsLoaderCallbacks = object : DistributionReadyBinTaskLoader.LoaderCallbacks(this) {
        @Suppress("IntroduceWhenSubject")
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<Part>>>, data: TaskLoaderResult<Array<Part>>) {
            supportLoaderManager.destroyLoader(LOADER_PART)

            dismissLoadingDialog()
            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val bins = data.value
            when {
                bins == null -> {
                    showError("No Mfg No Found")

                    viewModel.partBarcode.set("")
                    binding.editPartBarcode.requestFocus()
                    binding.editPartBarcode.selectAll()
                }
                else -> {

                    //  Build Drop Down List
                    val builder = DropDownDialog.Builder()
                    builder.title = "Distribution Ready Bins "
                    builder.adapter = DistributionReadyBinDialogAdapter::class.java
                    builder.extras = Bundle()

                    val listResults = ArrayList<Part>()

                    for (results in bins) {
                        if (results.bin?.code != null && listResults.none { it.bin!!.code == results.bin!!.code }) {
                            listResults.add(results)
                        }

                    }
                    builder.extras!!.putParcelableArrayList(
                            DistributionReadyBinDialogAdapter.ARGS_DETAILS, listResults)

                    val dialog = builder.create()
                    //dialog.setOnItemSelectedListener(dialogSummaryMfgNoSelectedListener)

                    dialog.show(supportFragmentManager, DIALOG_DETAILS)
                    binding.editPartBarcode.requestFocus()

                }
            }
        }
    }

    private val jobSummaryViewLoaderCallbacks = object : JobSummaryViewTaskLoader.LoaderCallbacks(this) {
        @Suppress("IntroduceWhenSubject")
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<JobSummaryView>>>, data: TaskLoaderResult<Array<JobSummaryView>>) {
            supportLoaderManager.destroyLoader(LOADER_MFG_NO)

            dismissLoadingDialog()
            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val results = data.value
            when {
                results == null -> {
                    showError("No Mfg No Found")

                    viewModel.partBarcode.set("")
                    binding.editPartBarcode.requestFocus()
                    binding.editPartBarcode.selectAll()
                }
                else -> {

                    //  Build Drop Down List
                    val builder = DropDownDialog.Builder()
                    builder.title = "Mfg No: " + viewModel.part.get()!!.mfgNo
                    builder.adapter = JobSummaryViewDialogAdapter::class.java
                    builder.extras = Bundle()

                    val listResults = ArrayList<JobSummaryView>()

                    for (resultss in results) {
                        listResults.add(resultss)

                    }
                    builder.extras!!.putParcelableArrayList(
                            JobSummaryViewDialogAdapter.ARGS_DETAILS, listResults)

                    val dialog = builder.create()
                    dialog.setOnItemSelectedListener(dialogSummaryMfgNoSelectedListener)

                    dialog.show(supportFragmentManager, DIALOG_DETAILS)
                    binding.editPartBarcode.requestFocus()

                }
            }
        }
    }

    val dialogSummaryMfgNoSelectedListener = object : DropDownDialog.OnItemSelectedListener {
        override fun onItemSelected(dialog: DropDownDialog, item: Parcelable?): Boolean {
            val detail = item as Part
            // Do Nothing
            return false
        }
    }


}