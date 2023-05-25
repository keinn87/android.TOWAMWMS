package com.globalsion.dialogs

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.globalsion.adapters.SimpleAdapter
import com.globalsion.dialogs.interfaces.DialogResultInterface
import com.globalsion.utils.AndroidUtil
import com.globalsion.utils.ClassUtil
import android.support.v7.widget.DividerItemDecoration
import com.towamwms.R


class DropDownDialog : AbstractDialog() {
    companion object {
        private const val ARGS_TITLE = "TITLE"
        private const val ARGS_ADAPTER_CLASS_NAME = "LOADER_CLASS_NAME"
        private const val ARGS_EXTRAS = "EXTRAS"

        const val DATA_SELECTED_ITEM = "SELECTED_ITEM"
    }

    private var adapter: Adapter? = null
    private var selectedItem: Parcelable? = null
    private var onItemSelectedListener: OnItemSelectedListener? = null

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        onItemSelectedListener = listener
    }
    fun setOnItemSelectedListener(listener: (dialog: DropDownDialog, selected: Parcelable?) -> Boolean) {
        onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(dialog: DropDownDialog, item: Parcelable?) : Boolean {
                return listener(dialog, item)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (adapter == null) {
            val clazz = ClassUtil.forName(arguments!!.getString(ARGS_ADAPTER_CLASS_NAME))
            adapter = clazz.newInstance() as Adapter
        }
        adapter!!.prepare(arguments!!.getBundle(ARGS_EXTRAS))

        dialog.setTitle(arguments!!.getString(ARGS_TITLE))
        val textTitle: TextView = dialog.findViewById(android.support.v4.R.id.title)
        textTitle.setPadding(textTitle.paddingLeft, textTitle.paddingTop, textTitle.paddingRight,
                AndroidUtil.convertDpToPixel(16f).toInt())

        val frameLayout = FrameLayout(inflater.context)
        //val padding = AndroidUtil.convertDpToPixel(8f).toInt()
        frameLayout.setPadding(0, 0, 0, 0)
        frameLayout.apply {
            val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams = params
        }

        val recyclerView = RecyclerView(inflater.context)
        recyclerView.apply {
            val params = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams = params

        }
        frameLayout.addView(recyclerView)

        val simpleAdapter = SimpleAdapter(
                adapter!!.contentLayoutId,
                adapter!!.contentVariableId,
                arrayListOf<Parcelable?>()
        )
        val layoutManager = LinearLayoutManager(inflater.context,
                LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(inflater.context.getDrawable(R.drawable.list_divider))
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = simpleAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        simpleAdapter.setItemClickListener { holder, _ ->
            val handled = onItemSelectedListener?.onItemSelected(this, holder.item) ?: false
            if (!handled) {
                selectedItem = holder.item
                dismiss()
            }
        }

        adapter!!.onLoadItems(inflater.context, simpleAdapter)

        return frameLayout
    }

    override fun onDialogResult(callback: DialogResultInterface) {
        val data = Bundle()
        data.putParcelable(DATA_SELECTED_ITEM, selectedItem)
        callback.onDialogResult(this, requestCode, data)
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val attributes = dialog.window.attributes
        attributes.horizontalMargin = 46f
        attributes.verticalMargin = 46f
        dialog.window.attributes = attributes
    }

    class Builder {
        var title: String = ""
        var adapter: Class<*>? = null
        var extras: Bundle? = null

        fun create() : DropDownDialog {
            if (title.isEmpty()) {
                throw IllegalArgumentException("title is required.")
            }
            if (adapter == null) {
                throw IllegalArgumentException("adapter is required.")
            }
            if (!Adapter::class.java.isAssignableFrom(adapter!!)) {
                throw IllegalArgumentException("adapter must implement '${Adapter::class.java.name}' interface.")
            }

            val dialog = DropDownDialog()
            dialog.arguments = Bundle()
            dialog.arguments!!.putString(ARGS_TITLE, title)
            dialog.arguments!!.putString(ARGS_ADAPTER_CLASS_NAME, adapter!!.name)
            dialog.arguments!!.putBundle(ARGS_EXTRAS, extras)
            return dialog
        }
    }

    interface Adapter {
        val contentLayoutId: Int
        val contentVariableId: Int

        fun prepare(extras: Bundle?)
        fun onLoadItems(context: Context, simpleAdapter: SimpleAdapter<Parcelable?>)
    }

    interface OnItemSelectedListener {
        fun onItemSelected(dialog: DropDownDialog, item: Parcelable?) : Boolean
    }
}