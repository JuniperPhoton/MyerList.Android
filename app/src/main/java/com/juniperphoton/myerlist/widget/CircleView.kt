package com.juniperphoton.myerlist.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

open class CircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint: Paint = Paint()

    open var color: Int = Color.WHITE
        set(value) {
            field = value
            paint.color = this.color
            invalidate()
        }

    init {
        paint.color = Color.WHITE
        paint.isDither = true
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), paint)
    }
}
