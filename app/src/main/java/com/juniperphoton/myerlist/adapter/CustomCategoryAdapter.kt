package com.juniperphoton.myerlist.adapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView

import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.util.CustomItemTouchHelper
import com.juniperphoton.myerlist.util.KeyboardUtil
import com.juniperphoton.myerlist.widget.CircleView

import java.util.Collections

import butterknife.BindView
import butterknife.ButterKnife

class CustomCategoryAdapter(private val context: Context) : BaseAdapter<ToDoCategory, CustomCategoryAdapter.CustomCategoryViewHolder>() {
    var onClickSelectCategory: ((ToDoCategory?) -> Unit)? = null

    private val helper = CustomItemTouchHelper(object : CustomItemTouchHelper.Callback() {
        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = CustomItemTouchHelper.UP or CustomItemTouchHelper.DOWN
            return CustomItemTouchHelper.Callback.makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val targetPos = target.adapterPosition - if (hasHeader) 1 else 0
            val pos = viewHolder.adapterPosition - if (hasHeader) 1 else 0
            if (hasHeader && targetPos < 0) {
                return false
            }
            Collections.swap(data!!, pos, targetPos)
            notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return false
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
        }
    })

    override fun onCreateItemViewHolder(parent: ViewGroup): CustomCategoryViewHolder {
        return CustomCategoryViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.row_custom_category, parent, false))
    }

    override fun onBindItemViewHolder(holder: CustomCategoryViewHolder, dataPosition: Int) {
        holder.bind(getData(dataPosition))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        helper.attachToRecyclerView(recyclerView)
    }

    inner class CustomCategoryViewHolder(itemView: View) : BaseViewHolder(itemView) {
        @JvmField
        @BindView(R.id.row_category_color_view_root)
        var colorRoot: View? = null

        @JvmField
        @BindView(R.id.row_category_color_view)
        var cateView: CircleView? = null

        @JvmField
        @BindView(R.id.row_category_content)
        var nameTextView: TextView? = null

        @JvmField
        @BindView(R.id.arrange_delete)
        var deleteView: View? = null

        @JvmField
        @BindView(R.id.arrange_thumb)
        var thumb: View? = null

        private var category: ToDoCategory? = null

        init {
            ButterKnife.bind(this, itemView)

            deleteView!!.setOnClickListener { removeData(adapterPosition - 1) }

            colorRoot!!.setOnClickListener {
                onClickSelectCategory?.invoke(category)
            }

            thumb!!.setOnTouchListener(View.OnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        helper.startDrag(this@CustomCategoryViewHolder)
                        return@OnTouchListener true
                    }
                }
                true
            })

            nameTextView!!.setOnClickListener {
                val root = LayoutInflater.from(context).inflate(R.layout.dialog_edit_name, null, false) as FrameLayout
                val editText = root.findViewById(R.id.edit_text_view) as EditText

                if (category != null) {
                    editText.setText(category!!.name)
                    editText.setSelection(editText.text.length)
                }

                val builder = AlertDialog.Builder(context)
                builder.setView(root)
                        .setTitle(R.string.edit_category_name_title)
                        .setPositiveButton(context.getString(R.string.ok_adding)) { dialog, _ ->
                            dialog.dismiss()
                            if (category != null && nameTextView != null) {
                                category!!.name = editText.text.toString()
                                nameTextView!!.text = editText.text.toString()
                            }
                        }
                        .setNegativeButton(context.getString(R.string.cancel_adding)) { dialog, _ -> dialog.dismiss() }.create().show()
                KeyboardUtil.show(context, editText, 50)
            }
        }

        fun bind(toDoCategory: ToDoCategory?) {
            category = toDoCategory
            cateView!!.setColor(category!!.intColor)
            nameTextView!!.text = category!!.name
        }
    }
}