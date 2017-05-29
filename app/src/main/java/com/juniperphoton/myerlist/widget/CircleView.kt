package com.juniperphoton.myerlist.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

open class CircleView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var color: Int = 0
    private val paint: Paint = Paint()

    init {
        paint.color = Color.WHITE
        paint.isDither = true
        paint.isAntiAlias = true
    }

    open fun setColor(color: Int) {
        this.color = color
        paint.color = this.color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), paint)
    }
}
