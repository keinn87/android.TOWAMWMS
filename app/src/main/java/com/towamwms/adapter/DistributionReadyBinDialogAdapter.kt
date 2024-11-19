package com.towamwms.adapter

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.globalsion.adapters.SimpleAdapter
import com.globalsion.dialogs.DropDownDialog
import com.towamwms.BR
import com.towamwms.R
import com.towamwms.entities.JobSummaryView
import com.towamwms.entities.Part

class DistributionReadyBinDialogAdapter : DropDownDialog.Adapter {
    companion object {
        const val ARGS_DETAILS = "DETAILS"
    }

    override val contentLayoutId: Int
        get() = R.layout.content_distribution_ready_bin
    override val contentVariableId: Int
        get() = BR.detail

    lateinit var details: List<Part>

    override fun prepare(extras: Bundle?) {
        details = extras!!.getParcelableArrayList(ARGS_DETAILS)
    }

    override fun onLoadItems(context: Context, simpleAdapter: SimpleAdapter<Parcelable?>) {
        for (detail in details) {
            simpleAdapter.addItem(detail)
        }
    }
}