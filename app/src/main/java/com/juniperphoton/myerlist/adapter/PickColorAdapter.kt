package com.juniperphoton.myerlist.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import butterknife.BindView
import butterknife.ButterKnife
import com.juniperphoton.myerlist.R

class PickColorAdapter(private val context: Context) : BaseAdapter<Int, PickColorAdapter.PickColorHolder>() {
    var onSelectColor: ((Int) -> Unit)? = null

    override fun onCreateItemViewHolder(parent: ViewGroup): PickColorHolder {
        return PickColorHolder(LayoutInflater.from(context).inflate(R.layout.row_pick_color, parent, false))
    }

    override fun onBindItemViewHolder(holder: PickColorHolder, dataPosition: Int) {
        holder.bind(getData(dataPosition))
        animateContainer(holder.itemView!!, dataPosition)
    }

    private fun animateContainer(container: View, position: Int) {
        val delay = 10L * position
        val duration = 400L

        val startAlpha = 0f
        val startTranslation = 40

        container.alpha = startAlpha
        container.translationX = startTranslation.toFloat()

        container.animate().alpha(1.0f)
                .translationX(0f)
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(DecelerateInterpolator())
                .start()
    }

    inner class PickColorHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.color_view)
        internal var tintView: View? = null

        @JvmField
        @BindView(R.id.color_picker_item_root)
        internal var root: ViewGroup? = null

        internal var color: Int? = null

        init {
            ButterKnife.bind(this, itemView)
            root!!.setOnClickListener {
                if (color != null) {
                    onSelectColor?.invoke(color!!)
                }
            }
        }

        fun bind(color: Int?) {
            tintView!!.setBackgroundColor(color!!)
            this.color = color
        }
    }
}
