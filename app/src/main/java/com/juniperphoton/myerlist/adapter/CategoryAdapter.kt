package com.juniperphoton.myerlist.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.toColor
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.widget.CircleView

class CategoryAdapter : BaseAdapter<ToDoCategory, CategoryAdapter.CategoryViewHolder>() {
    var onSelected: ((ToDoCategory, Int) -> Unit)? = null

    private var selectedIndex = -1

    override fun onCreateItemViewHolder(parent: ViewGroup): CategoryViewHolder {
        val view = LayoutInflater.from(App.instance).inflate(R.layout.row_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: CategoryViewHolder, dataPosition: Int) {
        holder.bind(getData(dataPosition))
        holder.itemView?.setOnClickListener(View.OnClickListener {
            if (dataPosition == selectedIndex) return@OnClickListener
            selectItem(dataPosition)
        })
    }

    fun selectItem(dataPosition: Int) {
        val category = getData(dataPosition)
        if (category.id != ToDoCategory.VALUE_PERSONALIZATION_ID) {
            val lastIndex = selectedIndex
            selectedIndex = dataPosition
            notifyItemChanged(getAdapterPosition(lastIndex))
            notifyItemChanged(getAdapterPosition(dataPosition))
        }
        onSelected?.invoke(category, dataPosition)
    }

    private fun getItemById(cateId: String): ToDoCategory? {
        return (0..data!!.size - 1)
                .map { getData(it) }
                .firstOrNull { it.id == Integer.valueOf(cateId) }
    }

    fun getItemIndexById(cateId: String): Int {
        val toDoCategory = getItemById(cateId)
        if (toDoCategory != null) {
            return data!!.indexOf(toDoCategory)
        }
        return -1
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.row_cate_color_view)
        var circleView: CircleView? = null

        @JvmField
        @BindView(R.id.row_cate_name)
        var nameView: TextView? = null

        @JvmField
        @BindView(R.id.row_cate_back)
        var backView: View? = null

        init {
            ButterKnife.bind(this@CategoryViewHolder, itemView)
        }

        fun bind(category: ToDoCategory) {
            circleView!!.color = category.color!!.toColor()
            nameView!!.text = category.name
            if (getDataPosition(adapterPosition) == selectedIndex) {
                setSelected()
            } else {
                setUnselected()
            }
        }

        fun setSelected() {
            backView!!.visibility = View.VISIBLE
        }

        fun setUnselected() {
            backView!!.visibility = View.GONE
        }
    }
}
