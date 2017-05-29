package com.juniperphoton.myerlist.util

import android.graphics.Color

fun String.toColor(): Int {
    return Color.parseColor(this)
}

fun Int.toColorString(): String {
    return String.format("#%06X", 0xFFFFFF and this)
}
