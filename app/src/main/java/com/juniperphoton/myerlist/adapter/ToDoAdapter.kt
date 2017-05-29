package com.juniperphoton.myerlist.adapter

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.model.ToDo
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.realm.RealmUtils
import com.juniperphoton.myerlist.util.CustomItemTouchHelper
import com.juniperphoton.myerlist.widget.CircleView

import java.util.Collections

import butterknife.BindView
import butterknife.ButterKnife

class ToDoAdapter : BaseAdapter<ToDo, ToDoAdapter.ToDoViewHolder>() {
    companion object {
        private val TAG = "ToDoAdapter"
    }

    var onArrangeCompleted: (() -> Unit)? = null
    var onUpdateDone: ((Int) -> Unit)? = null
    var onDelete: ((Int) -> Unit)? = null
    var onClickItem: ((Int, View) -> Unit)? = null
    var onClickRecover: ((Int) -> Unit)? = null

    var canDrag: Boolean = true

    private var recyclerView: RecyclerView? = null
    private val helper = CustomItemTouchHelper(object : CustomItemTouchHelper.Callback() {
        private val SWIPE_THRESHOLD = 0.4f

        private fun getToDoViewHolder(viewHolder: RecyclerView.ViewHolder): ToDoViewHolder {
            return viewHolder as ToDoViewHolder
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val toDo = getData(viewHolder.adapterPosition)
            if (!toDo.isValid) {
                return CustomItemTouchHelper.Callback.makeMovementFlags(0, 0)
            }
            if (toDo.deleted) {
                return CustomItemTouchHelper.Callback.makeMovementFlags(0, CustomItemTouchHelper.LEFT)
            }
            val dragFlags = CustomItemTouchHelper.UP or CustomItemTouchHelper.DOWN
            val swipeFlags = CustomItemTouchHelper.LEFT or CustomItemTouchHelper.RIGHT
            return CustomItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            RealmUtils.mainInstance.executeTransaction {
                Collections.swap(data!!, viewHolder.adapterPosition, target.adapterPosition)
                notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            }

            if (isItemValid(viewHolder.adapterPosition)) {
                return false
            }

            onArrangeCompleted?.invoke()
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.d(TAG, "onSwiped")
            Log.d(TAG, "direction" + direction)
            when (direction) {
                CustomItemTouchHelper.RIGHT -> {
                    if (isItemValid(viewHolder.adapterPosition)) {
                        return
                    }
                    getToDoViewHolder(viewHolder).toggleDone()
                    onUpdateDone?.invoke(viewHolder.adapterPosition)
                }
                CustomItemTouchHelper.LEFT -> {
                    if (isItemValid(viewHolder.adapterPosition)) {
                        return
                    }
                    onDelete?.invoke(viewHolder.adapterPosition)
                }
            }
            super.clearView(recyclerView, viewHolder)
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return SWIPE_THRESHOLD
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                 dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val holder = getToDoViewHolder(viewHolder)
            if (dX > recyclerView.width * SWIPE_THRESHOLD) {
                holder.setBackgroundGreen()
            } else if (dX < -recyclerView.width * SWIPE_THRESHOLD) {
                holder.setBackgroundRed()
            } else {
                holder.setBackgroundTransparent()
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            //getToDoViewHolder(viewHolder).setBackgroundTransparent();
        }
    })

    fun isItemValid(pos: Int): Boolean {
        val todo = getData(pos)
        return !todo.isManaged && todo.isLoaded && todo.isValid
    }

    override fun onCreateItemViewHolder(parent: ViewGroup): ToDoAdapter.ToDoViewHolder {
        val view = LayoutInflater.from(App.instance).inflate(R.layout.row_todo, parent, false)
        return ToDoViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: ToDoAdapter.ToDoViewHolder, dataPosition: Int) {
        holder.bind(holder.adapterPosition)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        helper.attachToRecyclerView(recyclerView)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getData(position).id!!.toLong()
    }

    inner class ToDoViewHolder(itemView: View) : BaseViewHolder(itemView) {
        @JvmField
        @BindView(R.id.row_todo_color_view)
        var circleView: CircleView? = null

        @JvmField
        @BindView(R.id.row_todo_content)
        var contentView: TextView? = null

        @JvmField
        @BindView(R.id.done_line)
        var doneView: View? = null

        @JvmField
        @BindView(R.id.arrange_thumb)
        var thumb: View? = null

        @JvmField
        @BindView(R.id.recover_icon)
        var recoverView: View? = null

        @JvmField
        @BindView(R.id.item_root)
        var root: View? = null

        private var isGreen: Boolean = false
        private var isRed: Boolean = false
        private var toDo: ToDo? = null

        init {
            ButterKnife.bind(this, itemView)
            root!!.setOnClickListener(View.OnClickListener {
                if (toDo == null || toDo!!.deleted) {
                    return@OnClickListener
                }
                onClickItem?.invoke(adapterPosition, circleView!!)
            })
        }

        fun bind(position: Int) {
            val todo = getData(position)
            if (!todo.isValid || !todo.isManaged) {
                return
            }
            toDo = todo

            val realm = RealmUtils.mainInstance
            realm.beginTransaction()
            val cate = Integer.valueOf(todo.cate)!!
            if (cate > 0) {
                val category = realm.where(ToDoCategory::class.java).equalTo(ToDoCategory.ID_KEY,
                        cate).findFirst()
                if (category != null) {
                    circleView!!.setColor(category.intColor)
                }
            } else if (cate == 0) {
                circleView!!.setColor(ContextCompat.getColor(App.instance, R.color.MyerListBlue))
            }
            realm.commitTransaction()

            if (todo.isdone == ToDo.IS_DONE) {
                doneView!!.visibility = View.VISIBLE
            } else {
                doneView!!.visibility = View.GONE
            }
            contentView!!.text = todo.content
            if (canDrag) {
                thumb!!.visibility = View.VISIBLE
                thumb!!.setOnTouchListener(View.OnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            helper.startDrag(this@ToDoViewHolder)
                            setBackgroundGrey()
                            return@OnTouchListener true
                        }
                    }
                    true
                })
            } else {
                thumb!!.visibility = View.GONE
            }
            if (todo.deleted) {
                thumb!!.visibility = View.GONE
                recoverView!!.visibility = View.VISIBLE
            } else {
                recoverView!!.visibility = View.GONE
            }
            recoverView!!.setOnClickListener {
                onClickRecover?.invoke(adapterPosition)
            }
        }

        fun toggleDone() {
            val todo = getData(adapterPosition)
            RealmUtils.mainInstance.executeTransaction {
                if (todo.isdone == ToDo.IS_DONE) {
                    todo.isdone = ToDo.IS_NOT_DONE
                } else {
                    todo.isdone = ToDo.IS_DONE
                }
            }
            if (todo.isdone == ToDo.IS_DONE) {
                doneView!!.visibility = View.VISIBLE
            } else {
                doneView!!.visibility = View.GONE
            }
        }

        fun setBackgroundGrey() {
            val fromColor = (rootView!!.background as ColorDrawable).color
            val greyColor = ContextCompat.getColor(App.instance, R.color.MyerListGray)
            animateColor(fromColor, greyColor)
        }

        fun setBackgroundGreen() {
            if (isGreen) return
            isRed = false
            val fromColor = (rootView!!.background as ColorDrawable).color
            val toColor = ContextCompat.getColor(App.instance, R.color.DoneGreenColor)
            animateColor(fromColor, toColor)
            isGreen = true
        }

        fun setBackgroundRed() {
            if (isRed) return
            isGreen = false
            val fromColor = (rootView!!.background as ColorDrawable).color
            val toColor = ContextCompat.getColor(App.instance, R.color.DeleteRedColor)
            animateColor(fromColor, toColor)
            isRed = true
        }

        fun animateColor(fromColor: Int, toColor: Int) {
            val valueAnimator = ValueAnimator.ofArgb(fromColor, toColor)
            valueAnimator.duration = 300
            valueAnimator.addUpdateListener { animation -> rootView!!.background = ColorDrawable(animation.animatedValue as Int) }
            valueAnimator.start()
        }

        fun setBackgroundTransparent() {
            isGreen = false
            isRed = false
            val fromColor = (rootView!!.background as ColorDrawable).color
            val toColor = Color.WHITE
            animateColor(fromColor, toColor)
        }
    }
}
