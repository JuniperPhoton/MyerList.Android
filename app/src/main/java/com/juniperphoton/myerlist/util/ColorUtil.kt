package com.juniperphoton.myerlist.util

import android.graphics.Color
import android.support.v4.content.ContextCompat
import com.juniperphoton.myerlist.App

fun String.toColor(): Int {
    return Color.parseColor(this)
}

fun Int.toColorString(): String {
    return String.format("#%06X", 0xFFFFFF and this)
}

fun Int.toResColor(): Int {
    return ContextCompat.getColor(App.instance, this)
}
