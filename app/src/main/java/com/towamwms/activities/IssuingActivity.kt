package com.towamwms.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.Loader
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import com.globalsion.dialogs.LoadingDialog
import com.globalsion.fragments.pageradapters.SimpleFragmentPagerAdapter
import com.globalsion.models.ActionLiveEvent
import com.globalsion.utils.DateUtil
import com.globalsion.utils.ToastUtil
import com.towamwms.MainApplication
import com.towamwms.R
import com.towamwms.databinding.ActivityIssuingBinding
import com.towamwms.dialogs.NewUserLoginDialog
import com.towamwms.dialogs.UserLoginDialog
import com.towamwms.entities.*
import com.towamwms.enums.EnumPartStatus
import com.towamwms.fragments.IssuingFragment
import com.towamwms.fragments.PicklistDetailFragment
import com.towamwms.models.FetchParam
import com.towamwms.models.InsertParam
import com.towamwms.models.TaskLoaderResult
import com.towamwms.services.UserApiService
import com.towamwms.services.UserSignApiService
import com.towamwms.taskloaders.InsertIssuingTaskLoader
import com.towamwms.taskloaders.PicklistTaskLoader
import com.towamwms.taskloaders.UserSignTaskLoader
import com.towamwms.taskloaders.UserTaskLoader
import com.towamwms.utils.ActivityUtil
import com.towamwms.utils.HashUtil
import com.towamwms.viewmodels.IssuingActivityVM

class IssuingActivity : AbstractTowaActivity<IssuingActivityVM>(), ViewPager.OnPageChangeListener {
    companion object {
        const val DIALOG_USER_SIGN = "USER_SIGN_DIALOG"
        const val DIALOG_NEW_USER_SIGN = "NEW_USER_SIGN_DIALOG"
        const val INTENT_ISSUING = "ISSUING"
        const val STATE_PAGE_POSITION = "PAGE_POSITION"
        const val STATE_ISSUING_CACHE = "ISSUING_CACHE"
        const val LOADER_PICKLIST = 1
        const val LOADER_USER_SIGN = 2
        const val LOADER_INSERT = 3
        const val LOADER_NEW_USER_SIGN = 4

        fun newIntent(context: Context, issuing: Issuing): Intent {
            val intent = Intent(context, IssuingActivity::class.java)
            intent.putExtra(INTENT_ISSUING, issuing)
            return intent
        }
    }

    lateinit var binding: ActivityIssuingBinding
    lateinit var pagerAdapter: SimpleFragmentPagerAdapter
    override val container: ViewGroup
        get() = binding.container

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewModel(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(IssuingActivityVM::class.java)
        viewModel.commandLoadPicklist.observe(this, Observer { loadPicklist(it!!) })
        viewModel.commandPerformLogin.observe(this, Observer { loadUserSigned(it!!) })
        viewModel.commandInsertRecord.observe(this, Observer { insertRecord() })
        viewModel.commandPerformNewLogin.observe(this, Observer { loadNewUserSigned(it!!) })

        if (savedInstanceState == null || !savedInstanceState.getBoolean(STATE_ISSUING_CACHE)) {
            viewModel.issuing.set(intent.getParcelableExtra(INTENT_ISSUING)!!)
        } else {
            try {
                val fileName = "${javaClass.name}.$STATE_ISSUING_CACHE.zip"
                val issuing = ActivityUtil.cacheLoad<Issuing>(fileName)
                viewModel.issuing.set(issuing)
            } catch (e: Exception) {
                viewModel.issuing.set(intent.getParcelableExtra(INTENT_ISSUING)!!)
                Log.e(javaClass.name, "Failed to load state from cache", e)
                finish()
            }
            //viewModel.issuing.set(savedInstanceState.getParcelable(STATE_ISSUING_CACHE)!!)
        }
    }

    override fun onCreateDataBinding(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_issuing)
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        pagerAdapter = SimpleFragmentPagerAdapter(supportFragmentManager)
        pagerAdapter.addItem(getString(R.string.title_issuing)) { IssuingFragment() }
        pagerAdapter.addItem(getString(R.string.title_picklist_details)) { PicklistDetailFragment() }

        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.addOnPageChangeListener(this)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.buttonSave.setOnClickListener { showNewUserSignDialog() }

        if (savedInstanceState != null) {
            binding.viewPager.currentItem = savedInstanceState.getInt(STATE_PAGE_POSITION)
        }
        onPageSelected(binding.viewPager.currentItem)
    }

    override fun onResume() {
        super.onResume()
        val picklistLoader = supportLoaderManager.getLoader<TaskLoaderResult<*>>(LOADER_PICKLIST)
        val userSignLoader = supportLoaderManager.getLoader<TaskLoaderResult<*>>(LOADER_USER_SIGN)
        val newUserSignLoader = supportLoaderManager.getLoader<TaskLoaderResult<*>>(LOADER_NEW_USER_SIGN)
        val insertLoader = supportLoaderManager.getLoader<TaskLoaderResult<*>>(LOADER_INSERT)
        val dialog = supportFragmentManager.findFragmentByTag(DIALOG_LOADING) as LoadingDialog?

        if (picklistLoader != null || userSignLoader != null || insertLoader != null) {
            when {
                picklistLoader != null -> showLoadingDialog(R.string.message_loading_picklist)
                userSignLoader != null -> showLoadingDialog(R.string.message_loading_user_sign)
                newUserSignLoader != null -> showLoadingDialog(R.string.message_loading_user_sign)
                insertLoader != null -> showLoadingDialog(R.string.message_saving)
            }
        } else {
            dialog?.dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(STATE_PAGE_POSITION, binding.viewPager.currentItem)
        outState?.putBoolean(STATE_ISSUING_CACHE, true)

        try {
            val fileName = "${javaClass.name}.$STATE_ISSUING_CACHE.zip"
            ActivityUtil.cacheSave(fileName, viewModel.issuing.get()!!)
        } catch (e: Exception) {
            Log.e(javaClass.name, "Failed to save state to cache.", e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_trans, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_save -> showNewUserSignDialog()
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

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
//        when (position) {
//            0 -> binding.buttonSave.show()
//            1 -> binding.buttonSave.hide()
//        }
    }

    fun showUserSignDialog() {
        if (!validateRecord()) {
            return
        }

        var userLoginDialog = supportFragmentManager
                .findFragmentByTag(DIALOG_USER_SIGN) as UserLoginDialog?

        userLoginDialog?.dismiss()
        viewModel.loginUsername.set("")
        viewModel.loginPassword.set("")

        userLoginDialog = UserLoginDialog.newInstance(
                getString(R.string.title_user_sign),
                IssuingActivityVM::class.java)
        userLoginDialog.show(supportFragmentManager, DIALOG_USER_SIGN)
    }

    // Sign in with ID & Username
    fun showNewUserSignDialog() {
        if (!validateRecord()) {
            return
        }

        var newUserLoginDialog = supportFragmentManager
                .findFragmentByTag(DIALOG_NEW_USER_SIGN) as NewUserLoginDialog?

        newUserLoginDialog?.dismiss()
        viewModel.loginNewUsername.set("")


        newUserLoginDialog = NewUserLoginDialog.newInstance(
                getString(R.string.title_user_sign),
                IssuingActivityVM::class.java)
        newUserLoginDialog.show(supportFragmentManager, DIALOG_NEW_USER_SIGN)

    }


    fun loadPicklist(partBarcode: String) {
        if (TextUtils.isEmpty(partBarcode)) {
            showError(R.string.message_please_input_part_barcode)
            return
        }

        val args = Bundle()
        val param = FetchParam(MainApplication.loginUser!!)
        args.putString(PicklistTaskLoader.ARGS_PART_BARCODE, partBarcode)
        args.putInt(PicklistTaskLoader.ARGS_METHOD, PicklistTaskLoader.METHOD_GET_RECORD_BY_PART_BARCODE)

        supportLoaderManager.destroyLoader(LOADER_PICKLIST)
        showLoadingDialog(getString(R.string.message_loading_part))

        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_PICKLIST, args, picklistLoaderCallbacks).forceLoad()
    }

    fun loadUserSigned(value: Pair<String?, String?>) {
        val username = value.first ?: ""
        val password = value.second ?: ""
        val args = Bundle()
        val param = FetchParam(MainApplication.loginUser!!)
        param.filters[UserApiService.FILTER_USERNAME] = username
        param.filters[UserApiService.FILTER_PASSWORD_HASH] = HashUtil.hash(password)

        supportLoaderManager.destroyLoader(LOADER_PICKLIST)
        showLoadingDialog(getString(R.string.message_loading_user_sign))

        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_USER_SIGN, args, userSignedLoaderCallbacks).forceLoad()
    }



    fun loadNewUserSigned(loginNewUsername: String?) {
        if (TextUtils.isEmpty(loginNewUsername)) {
            showError(R.string.message_please_scan_user_qr)
            return
        }

        if(!loginNewUsername!!.contains(",")){
            showError(R.string.message_user_qr_fail)
            val newUserLoginDialog = supportFragmentManager
                    .findFragmentByTag(DIALOG_NEW_USER_SIGN) as NewUserLoginDialog?
            newUserLoginDialog?.dismiss()
            return
        }

        val newLogin = loginNewUsername
        val delim = ","

        val arr = newLogin!!.split(delim).toTypedArray()

        val loginId = arr[0]
        val username = arr[1]

        val args = Bundle()

        val param = FetchParam(MainApplication.loginUser!!)
        param.filters[UserSignApiService.FILTER_ID] = loginId.toLong()
        param.filters[UserSignApiService.FILTER_USERNAME] = username

        supportLoaderManager.destroyLoader(LOADER_PICKLIST)
        showLoadingDialog(getString(R.string.message_loading_user_sign))

        param.toBundle(args)
        supportLoaderManager.initLoader(LOADER_USER_SIGN, args, userSignedLoaderCallbacks).forceLoad()
    }

    fun insertRecord() {
        val param = InsertParam(MainApplication.loginUser!!, viewModel.issuing.get()!!)

        supportLoaderManager.destroyLoader(LOADER_INSERT)
        showLoadingDialog(getString(R.string.message_saving))

        val bundle = Bundle()
        param.toBundle(bundle)
        supportLoaderManager.initLoader(LOADER_INSERT, bundle, insertLoaderCallbacks).forceLoad()
    }

    private fun validateRecord(): Boolean {
        var editTextFocusEvent: ActionLiveEvent<Pair<Int, Any?>>? = null

        var hasError = false
        if (viewModel.issuing.get()!!.picklistDetails!!.isEmpty()) {
            val param = Pair(
                    IssuingActivityVM.EDITOR_ERROR_SET,
                    getText(R.string.message_please_input_part_barcode)
            )

            viewModel.commandEditPartBarcode.call(param)
            hasError = true

            if (editTextFocusEvent == null) {
                editTextFocusEvent = viewModel.commandEditPartBarcode
            }
        } else {
            viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_ERROR_CLEAR, null))
        }

        if (hasError) {
            showError(R.string.message_please_input_required_fields)

            editTextFocusEvent!!.call(Pair(IssuingActivityVM.EDITOR_FOCUS, null))
            editTextFocusEvent.call(Pair(IssuingActivityVM.EDITOR_SELECT_ALL, null))
        }
        return !hasError
    }

    private val picklistLoaderCallbacks = object : PicklistTaskLoader.LoaderCallbacks(this) {
        @Suppress("IntroduceWhenSubject")
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<Picklist>>>, data: TaskLoaderResult<Array<Picklist>>) {
            supportLoaderManager.destroyLoader(LOADER_PICKLIST)

            dismissLoadingDialog()
            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val picklist = data.value!!.singleOrNull()
            when {
                picklist == null -> {
                    showError(R.string.message_picklist_doesnt_exists)

                    viewModel.partBarcode.set("")
                    viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_FOCUS, null))
                    viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_SELECT_ALL, null))
                }
                else -> {
                    val details = picklist.details?.filter { it.part?.status == EnumPartStatus.PICKED }?.toList()
                    if (details?.size ?: 0 == 0) {
                        val format = getString(R.string.message_picklist_empty_picked)
                        showError(String.format(format, picklist.documentNo ?: ""))

                        viewModel.partBarcode.set("")
                        viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_FOCUS, null))
                        viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_SELECT_ALL, null))
                    } else {
                        details!!.forEach {
                            val part = Part()
                            part.item = Item()
                            part.id = it.part!!.id
                            part.barcode = it.part!!.barcode
                            part.rCode = it.part!!.rCode
                            part.drawingNo = it.part!!.drawingNo
                            part.quantity = it.part!!.quantity
                            part.item!!.id = it.part!!.item!!.id
                            part.item!!.code = it.part!!.item!!.code

                            it.part = part
                        }



                        viewModel.issuing.get()!!.picklistDetails = LinkedHashSet(details)
                        viewModel.issuing.get()!!.mfgNo = picklist.mfgNo
                        viewModel.lineOfItems.set(details.size.toString())
                        viewModel.issuing.notifyChange()
                        viewModel.commandReloadPicklistDetails.call(0)

                        viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_ERROR_CLEAR, null))
                        viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_FOCUS, null))
                        viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_SELECT_ALL, null))

                        ToastUtil.show(this@IssuingActivity,
                                R.string.message_picklist_loaded, Toast.LENGTH_LONG)
                        binding.viewPager.currentItem = 1
                    }
                }
            }
        }
    }

    private val userSignedLoaderCallbacks = object : UserSignTaskLoader.LoaderCallbacks(this) {
        @Suppress("IntroduceWhenSubject")
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Array<User>>>, data: TaskLoaderResult<Array<User>>) {
            supportLoaderManager.destroyLoader(LOADER_USER_SIGN)

            dismissLoadingDialog()
            if (!data.success) {
                showLoaderException(data.exception)
                return
            }

            val user = data.value!!.singleOrNull()
            when {
                user == null -> {
                    showError(R.string.message_user_qr_fail)
                    val newUserLoginDialog = supportFragmentManager
                            .findFragmentByTag(DIALOG_NEW_USER_SIGN) as NewUserLoginDialog?
                    newUserLoginDialog?.dismiss()
                }
                else -> {
                    viewModel.issuing.get()!!.userSigned = user
                    viewModel.issuing.notifyChange()

                    Handler(Looper.getMainLooper()).post {
                        val newUserLoginDialog = supportFragmentManager
                                .findFragmentByTag(DIALOG_NEW_USER_SIGN) as NewUserLoginDialog?
                        newUserLoginDialog?.dismiss()
                        viewModel.commandInsertRecord.call(0)
                    }
                }
            }
        }
    }

    private val insertLoaderCallbacks = object : InsertIssuingTaskLoader.LoaderCallbacks(this) {
        override fun onLoadFinished(loader: Loader<TaskLoaderResult<Issuing>>, data: TaskLoaderResult<Issuing>) {
            supportLoaderManager.destroyLoader(LOADER_INSERT)

            dismissLoadingDialog()

            val newIssuing = Issuing()
            newIssuing.initialize()
            newIssuing.documentNo = "<<Auto Number>>"
            newIssuing.documentDate = DateUtil.today()
            newIssuing.userSigned = null

            if (!data.success) {
                showLoaderException(data.exception)
            } else {
                val format = getText(R.string.message_issuing_saved).toString()
                ToastUtil.show(this@IssuingActivity,
                        String.format(format, data.value!!.documentNo), Toast.LENGTH_LONG)

                snackbar.dismiss()
            }

            viewModel.issuing.set(newIssuing)
            viewModel.partBarcode.set("")
            viewModel.commandReloadPicklistDetails.call(0)
            viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_FOCUS, null))
            viewModel.commandEditPartBarcode.call(Pair(IssuingActivityVM.EDITOR_SELECT_ALL, null))

            binding.viewPager.currentItem = 0
        }
    }
}