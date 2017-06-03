package com.juniperphoton.myerlist.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.support.v4.content.ContextCompat
import android.view.View
import com.juniperphoton.myerlist.App

fun Context.getDpi(): Float = this.resources.displayMetrics.density

fun Context.dpToPixel(valueInDp: Int): Int = (valueInDp * this.getDpi()).toInt()

fun Context.getVersionCode(): Int {
    try {
        val info = packageManager.getPackageInfo(packageName, 0)
        return info.versionCode
    } catch (e: Exception) {
        e.printStackTrace()
        return -1
    }
}

fun Int.getResString(): String? {
    return App.instance!!.getString(this)
}

fun Int.getResColor(): Int? {
    return ContextCompat.getColor(App.instance, this)
}

fun Context.getVersionName(): String? {
    try {
        val info = packageManager.getPackageInfo(packageName, 0)
        return info.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun View.getActivity(): Activity? {
    var context = context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}