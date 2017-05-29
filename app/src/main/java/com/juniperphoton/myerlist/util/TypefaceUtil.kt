package com.juniperphoton.myerlist.util

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView

import java.util.HashMap

object TypefaceUtil {
    private var typefaceHashMap: HashMap<String, Typeface>? = null

    fun setTypeFace(textView: TextView, path: String, context: Context) {
        if (typefaceHashMap == null) {
            typefaceHashMap = HashMap<String, Typeface>()
        }
        var typeface: Typeface? = typefaceHashMap!![path]
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.assets, path)
            typefaceHashMap!!.put(path, typeface)
        }
        textView.typeface = typeface
    }
}