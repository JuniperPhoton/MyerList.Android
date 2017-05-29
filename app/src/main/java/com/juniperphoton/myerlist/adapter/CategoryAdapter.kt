package com.juniperphoton.myerlist.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
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
        holder.rootView?.setOnClickListener(View.OnClickListener {
            if (dataPosition == selectedIndex) return@OnClickListener
            selectItem(dataPosition)
        })
    }

    fun selectItem(position: Int) {
        val category = getData(position)
        if (category.id != ToDoCategory.PERSONALIZATION_ID) {
            val lastIndex = selectedIndex
            selectedIndex = position
            notifyItemChanged(lastIndex)
            notifyItemChanged(position)
        }
        onSelected?.invoke(category, position)
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

    inner class CategoryViewHolder(itemView: View) : BaseViewHolder(itemView) {
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
            circleView!!.setColor(Color.parseColor(category.color))
            nameView!!.text = category.name
            if (adapterPosition == selectedIndex) {
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