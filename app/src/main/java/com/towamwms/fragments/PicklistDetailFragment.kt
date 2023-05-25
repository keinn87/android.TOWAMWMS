package com.towamwms.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.globalsion.adapters.SimpleAdapter
import com.globalsion.fragments.AbstractFragment
import com.towamwms.BR
import com.towamwms.R
import com.towamwms.databinding.FragmentPicklistDetailBinding
import com.towamwms.entities.PicklistDetail
import com.towamwms.viewmodels.IssuingActivityVM

class PicklistDetailFragment : AbstractFragment() {

    lateinit var adapter : SimpleAdapter<PicklistDetail>
    lateinit var binding : FragmentPicklistDetailBinding
    lateinit var viewModel : IssuingActivityVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_picklist_detail, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(IssuingActivityVM::class.java)
        binding.viewModel = viewModel

        adapter = SimpleAdapter(R.layout.content_picklist_detail, BR.picklistDetail, viewModel.issuing.get()!!.picklistDetails!!)
        val layoutManager = LinearLayoutManager(context!!,
                LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(context!!,
                DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(context!!.getDrawable(R.drawable.list_divider))
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.commandReloadPicklistDetails.observe(this, Observer { commandReloadPicklistDetails() })
    }

    fun commandReloadPicklistDetails() {
        adapter.items.clear()
        adapter.items.addAll(viewModel.issuing.get()!!.picklistDetails!!)
        adapter.notifyDataSetChanged()
    }
}