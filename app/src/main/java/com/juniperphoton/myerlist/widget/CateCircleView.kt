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
    private val darkPaint = Paint()
    private val paint = Paint()

    private var radius = 0

    override var color: Int = Color.WHITE
        set(value) {
            field = value
            paint.color = value
            darkPaint.color = Color.argb(255, (Color.red(value) * 0.4).toInt(),
                    (Color.green(value) * 0.4).toInt(),
                    (Color.blue(value) * 0.4).toInt())
            invalidate()
        }

    private var valueAnimator: ValueAnimator? = null

    var inSelected: Boolean = false
        set(newValue) {
            if (field == newValue) {
                return
            }
            field = newValue
            val value = (layoutParams.width / 2 * 0.5).toInt()
            valueAnimator = ValueAnimator.ofInt(if (newValue) 0 else value, if (newValue) value else 0)
            valueAnimator?.let {
                it.addUpdateListener { animation ->
                    radius = animation.animatedValue as Int
                    invalidate()
                }
                it.duration = 300
                it.start()
            }
        }

    init {
        background = ContextCompat.getDrawable(ctx, R.drawable.ripple_borderless)
        darkPaint.isDither = true
        darkPaint.isAntiAlias = true
        paint.isDither = true
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), darkPaint)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2 * 0.8).toInt().toFloat(), paint)
        if (inSelected) {
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius.toFloat(), darkPaint)
        }
    }
}