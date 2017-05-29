package com.juniperphoton.myerlist.util

import android.graphics.Color

fun String.toColor(): Int {
    return Color.parseColor(this)
}

fun Int.toColorString(): String {
    return String.format("#%06X", 0xFFFFFF and this)
}

object ColorUtil {
    fun makeColorDarker(originalColor: Int): Int {
        val red = Color.red(originalColor)
        val green = Color.green(originalColor)
        val blue = Color.blue(originalColor)
        return Color.argb(255, (red * 0.7).toInt(), (green * 0.7).toInt(), (blue * 0.7).toInt())
    }

    fun generateColors(): MutableList<Int> {
        return mutableListOf("#F75B44",
                "#F73215",
                "#F7445B",
                "#E1184B",
                "#C11943",
                "#80224C",
                "#66436F",
                "#713A80",
                "#4D3A80",
                "#F75B44",
                "#352F44",
                "#474E88",
                "#2E3675",
                "#2A2E51",
                "#417C98",
                "#6FD1FF",
                "#3CBBF7",
                "#217CDC",
                "#4CAFFF",
                "#5474C1",
                "#317CA0",
                "#39525F",
                "#4F9595",
                "#2C8D8D",
                "#00BEBE",
                "#2B8A78",
                "#3FBEA6",
                "#3FBE7D",
                "#1C9B5A",
                "#5A9849",
                "#739849",
                "#C9D639",
                "#D6CD00",
                "#F7C142",
                "#F7D842",
                "#F79E42",
                "#FF8726").map {
            it.toColor()
        }.toMutableList()
    }
}
