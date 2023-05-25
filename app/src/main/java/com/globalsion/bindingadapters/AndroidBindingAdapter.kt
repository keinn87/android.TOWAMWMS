package com.globalsion.bindingadapters

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.os.Parcelable
import android.widget.TextView
import com.globalsion.views.DropDownEditText
import com.globalsion.views.SimpleDropDownEditText

object AndroidBindingAdapter {
    @BindingAdapter("onEditorAction")
    @JvmStatic
    fun textViewSetOnEditorActionListener(view: TextView, listener: TextView.OnEditorActionListener) {
        view.setOnEditorActionListener(listener)
    }

    @BindingAdapter("onItemSelected")
    @JvmStatic
    fun dropDownEditTextSetOnItemSelectedListener(view: DropDownEditText, listener: DropDownEditText.OnItemSelectedListener) {
        view.setOnItemSelectedListener(listener)
    }

    @BindingAdapter("selectedItem")
    @JvmStatic
    fun dropDownEditTextSetSelectedItem(view: DropDownEditText, selectedItem: Parcelable?) {
        view.selectedItem = selectedItem
    }

    @InverseBindingAdapter(attribute = "selectedItem")
    @JvmStatic
    fun dropDownEditTextGetSelectedItem(view: DropDownEditText) : Parcelable? {
        return view.selectedItem
    }

    @BindingAdapter("adapter")
    @JvmStatic
    fun dropDownEditTextSetAdapter(view: DropDownEditText, adapterClass: Class<*>) {
        view.adapter = adapterClass
    }

    @BindingAdapter("onItemSelected")
    @JvmStatic
    fun simpleDropDownEditTextSetOnItemSelectedListener(view: SimpleDropDownEditText, listener: SimpleDropDownEditText.OnItemSelectedListener) {
        view.setOnItemSelectedListener(listener)
    }

    @BindingAdapter("selectedItem")
    @JvmStatic
    fun simpleDropDownEditTextSetSelectedItem(view: SimpleDropDownEditText, selectedItem: String) {
        view.selectedItem = selectedItem
    }

    @InverseBindingAdapter(attribute = "selectedItem")
    @JvmStatic
    fun simpleDropDownEditTextGetSelectedItem(view: SimpleDropDownEditText): String {
        return view.selectedItem
    }

    @BindingAdapter("items")
    @JvmStatic
    fun simpleDropDownEditTextSetItems(view: SimpleDropDownEditText, items: Array<String>) {
        view.items.clear()
        view.items.addAll(items)
    }
}