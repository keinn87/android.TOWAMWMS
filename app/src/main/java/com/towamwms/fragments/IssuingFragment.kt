package com.towamwms.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.globalsion.fragments.AbstractFragment
import com.towamwms.R
import com.towamwms.databinding.FragmentIssuingBinding
import com.towamwms.viewmodels.IssuingActivityVM

class IssuingFragment : AbstractFragment() {
    lateinit var viewModel : IssuingActivityVM
    lateinit var binding : FragmentIssuingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuing, container, false)
        viewModel = ViewModelProviders.of(activity!!).get(IssuingActivityVM::class.java)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.commandEditPartBarcode.observe(this, Observer { commandEditPartBarcode(it!!) })
    }

    fun commandEditPartBarcode(args: Pair<Int, Any?>) {
        when (args.first) {
            IssuingActivityVM.EDITOR_FOCUS -> {
                binding.editPartBarcode.requestFocus()
            }
            IssuingActivityVM.EDITOR_SELECT_ALL -> {
                binding.editPartBarcode.selectAll()
            }
            IssuingActivityVM.EDITOR_ERROR_SET -> {
                binding.editPartBarcode.error = args.second as CharSequence
            }
            IssuingActivityVM.EDITOR_ERROR_CLEAR -> {
                binding.editPartBarcode.error = null
            }
            else -> {
                throw UnsupportedOperationException()
            }
        }
    }
}