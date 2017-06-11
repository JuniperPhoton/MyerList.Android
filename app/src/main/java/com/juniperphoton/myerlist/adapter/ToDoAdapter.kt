package com.juniperphoton.myerlist.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.toResColor
import com.juniperphoton.myerlist.model.ToDo
import com.juniperphoton.myerlist.model.ToDoCategory
import com.juniperphoton.myerlist.util.CustomItemTouchHelper
import com.juniperphoton.myerlist.widget.CircleView
import io.realm.Realm

class ToDoAdapter : BaseAdapter<ToDo, ToDoAdapter.ToDoViewHolder>() {
    companion object {
        private const val TAG = "ToDoAdapter"
    }

    var onArrangeCompleted: (() -> Unit)? = null
    var onUpdateDone: ((Int) -> Unit)? = null
    var onDelete: ((Int) -> Unit)? = null
    var onClickItem: ((Int, View) -> Unit)? = null
    var onClickRecover: ((Int) -> Unit)? = null

    var canDrag: Boolean = true

    private var recyclerView: RecyclerView? = null

    private val helper = CustomItemTouchHelper(object : CustomItemTouchHelper.Callback() {
        private val SWIPE_THRESHOLD = 0.3f

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

        override fun onMoved(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, fromPos: Int, target: RecyclerView.ViewHolder?, toPos: Int, x: Int, y: Int) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)

            Realm.getDefaultInstance().executeTransaction { realm ->
                val from = viewHolder!!.adapterPosition
                val to = target!!.adapterPosition

                val fromItem = realm.where(ToDo::class.java).equalTo(ToDo.KEY_POSITION, from)
                        .findFirst()
                val toItem = realm.where(ToDo::class.java).equalTo(ToDo.KEY_POSITION, to)
                        .findFirst()

                val fromPosition = fromItem.position
                val toPosition = toItem.position
                fromItem.position = toPosition
                toItem.position = fromPosition
                notifyItemMoved(from, to)
            }

            onArrangeCompleted?.invoke()
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.d(TAG, "onSwiped")
            Log.d(TAG, "direction" + direction)
            when (direction) {
                CustomItemTouchHelper.RIGHT -> {
                    if (isItemValid(viewHolder.adapterPosition)) {
                        return
                    }
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

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
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

    inner class ToDoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

            var color: Int = R.color.MyerListBlue.toResColor()

            var realm = Realm.getDefaultInstance()
            val cate = Integer.valueOf(todo.cate)!!
            if (cate > 0) {
                val category = realm.where(ToDoCategory::class.java).equalTo(ToDoCategory.KEY_ID,
                        cate).findFirst()
                if (category != null) {
                    color = category.intColor
                }
            }

            circleView?.color = color

            if (todo.isdone == ToDo.VALUE_DONE) {
                doneView!!.visibility = View.VISIBLE
            } else {
                doneView!!.visibility = View.GONE
            }
            contentView!!.text = todo.content
            if (canDrag) {
                thumb!!.visibility = View.VISIBLE
                thumb!!.setOnTouchListener(View.OnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            helper.startDrag(this@ToDoViewHolder)
                            return@OnTouchListener true
                        }
                    }
                    return@OnTouchListener false
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
    }
}
