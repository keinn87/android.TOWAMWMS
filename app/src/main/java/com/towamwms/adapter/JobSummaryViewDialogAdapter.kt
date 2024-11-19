package com.towamwms.adapter

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.globalsion.adapters.SimpleAdapter
import com.globalsion.dialogs.DropDownDialog
import com.towamwms.BR
import com.towamwms.R
import com.towamwms.entities.JobSummaryView

class JobSummaryViewDialogAdapter : DropDownDialog.Adapter {
    companion object {
        const val ARGS_DETAILS = "DETAILS"
    }

    override val contentLayoutId: Int
        get() = R.layout.content_job_summary_view
    override val contentVariableId: Int
        get() = BR.detail

    lateinit var details: List<JobSummaryView>

    override fun prepare(extras: Bundle?) {
        details = extras!!.getParcelableArrayList(ARGS_DETAILS)
    }

    override fun onLoadItems(context: Context, simpleAdapter: SimpleAdapter<Parcelable?>) {
        for (detail in details) {
            simpleAdapter.addItem(detail)
        }
    }
}