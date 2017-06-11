package com.juniperphoton.myerlist.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import java.util.*

abstract class BaseAdapter<T, U : RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val HEADER = 1
        private val ITEM = 1 shl 1
        private val FOOTER = 1 shl 2
    }

    var data: MutableList<T>? = ArrayList()
        private set
        get() = field

    var headerView: View? = null
    var footerView: View? = null

    val hasHeader: Boolean
        get() = headerView != null
    val hasFooter: Boolean
        get() = footerView != null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        parent.clipChildren = false
        return when (viewType) {
            HEADER -> BaseViewHolder(headerView)
            FOOTER -> BaseViewHolder(footerView)
            else -> onCreateItemViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM) {
            val index: Int
            if (headerView != null) {
                index = holder.adapterPosition - 1
            } else {
                index = holder.adapterPosition
            }
            onBindItemViewHolder(holder as U, index)
        }
    }

    protected abstract fun onCreateItemViewHolder(parent: ViewGroup): U

    protected abstract fun onBindItemViewHolder(holder: U, dataPosition: Int)

    override fun getItemCount(): Int {
        var count = 0
        if (data != null) count += data!!.size
        if (headerView != null) count++
        if (footerView != null) count++
        return count
    }

    override fun getItemViewType(position: Int): Int {
        if (headerView != null) {
            if (position == 0) return HEADER
            if (data != null && position < data!!.size - 1) return ITEM
        } else {
            if (data != null && position < data!!.size) return ITEM
        }
        if (footerView != null && position == itemCount - 1) {
            return FOOTER
        }
        return ITEM
    }

    fun refreshData(data: MutableList<T>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getData(dataIndex: Int): T {
        return data!![dataIndex]
    }

    fun addData(item: T) {
        data!!.add(item)
        notifyItemInserted(data!!.indexOf(item) + if (headerView != null) 1 else 0)
    }

    fun removeData(index: Int) {
        data!!.removeAt(index)
        notifyItemRemoved(if (headerView != null) index + 1 else index)
    }
}

open class BaseViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
