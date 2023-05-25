package com.globalsion.adapters

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/** Generic list adapter for [RecyclerView] */
@Suppress("CanBePrimaryConstructorProperty", "unused")
open class SimpleAdapter<T>(@LayoutRes layoutId: Int, variableId: Int, models: Iterable<T>)
        : RecyclerView.Adapter<SimpleAdapter.ViewHolder<T>>() {
    var items = ArrayList<T>()
    val layoutId = layoutId
    val variableId = variableId
    private var itemClickListener: ItemClickListener<T>? = null
    private var itemLongClickListener: ItemLongClickListener<T>? = null

    fun setItemClickListener(listener: (holder: ViewHolder<T>, position: Int) -> Unit) {
        itemClickListener = object : ItemClickListener<T> {
            override fun onItemClick(holder: ViewHolder<T>, position: Int) {
                listener(holder, position)
            }
        }
    }

    fun setItemClickListener(listener: ItemClickListener<T>) {
        itemClickListener = listener
    }

    fun setItemLongClickListener(listener: (holder: ViewHolder<T>, position: Int) -> Boolean) {
        itemLongClickListener = object : ItemLongClickListener<T> {
            override fun onItemLongClick(holder: ViewHolder<T>, position: Int): Boolean {
                return listener(holder, position)
            }
        }
    }

    fun setItemLongClickListener(listener: ItemLongClickListener<T>) {
        itemLongClickListener = listener
    }

    init {
        this.items.addAll(models)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil
                .inflate(inflater, layoutId, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(variableId, items[position])
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(holder, position) }
        holder.itemView.setOnLongClickListener {
            itemLongClickListener?.onItemLongClick(holder, position) ?: false
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(item: T): Boolean {
        val result = items.add(item)
        notifyItemInserted(items.lastIndex)
        return result
    }

    fun addItem(index: Int, item: T) {
        items.add(index, item)
        notifyItemInserted(index)
    }

    fun swapItem(index: Int, item: T): T {
        val oldItem = items[index]
        items[index] = item
        notifyItemChanged(index)
        return oldItem
    }

    fun removeItem(index: Int): T {
        val item = items.removeAt(index)
        notifyItemRemoved(index)
        return item
    }


    @Suppress("CanBePrimaryConstructorProperty")
    open class ViewHolder<T>(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: ViewDataBinding = binding
        var item: T? = null

        open fun bind(variableId: Int, item: T) {
            this.item = item
            binding.setVariable(variableId, item)
            binding.executePendingBindings()
        }
    }

    interface ItemClickListener<T> {
        fun onItemClick(holder: ViewHolder<T>, position: Int)
    }

    interface ItemLongClickListener<T> {
        fun onItemLongClick(holder: ViewHolder<T>, position: Int): Boolean
    }
}