package com.juniperphoton.myerlist.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet

import com.juniperphoton.myerlist.R

class CateCircleView(ctx: Context, attrs: AttributeSet?) : CircleView(ctx, attrs) {
    private var selected: Boolean = false

    private val darkPaint = Paint()
    private val paint = Paint()

    private var radius = 0

    init {
        background = ContextCompat.getDrawable(ctx, R.drawable.ripple_borderless)
        darkPaint.isDither = true
        darkPaint.isAntiAlias = true
        paint.isDither = true
        paint.isAntiAlias = true
    }

    override fun setColor(color: Int) {
        super.setColor(color)
        paint.color = color
        darkPaint.color = Color.argb(255, (Color.red(color) * 0.4).toInt(),
                (Color.green(color) * 0.4).toInt(),
                (Color.blue(color) * 0.4).toInt())
    }

    override fun setSelected(selected: Boolean) {
        if (this.selected == selected) {
            return
        }
        this.selected = selected
        val value = (layoutParams.width / 2 * 0.5).toInt()
        val valueAnimator = ValueAnimator.ofInt(if (selected) 0 else value, if (selected) value else 0)
        valueAnimator.addUpdateListener { animation ->
            radius = animation.animatedValue as Int
            invalidate()
        }
        valueAnimator.duration = 300
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), darkPaint)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2 * 0.8).toInt().toFloat(), paint)
        if (selected) {
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius.toFloat(), darkPaint)
        }
    }
}