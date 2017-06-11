package com.juniperphoton.myerlist.extension

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import com.juniperphoton.myerlist.App
import org.greenrobot.eventbus.EventBus

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

fun Int.getResString(): String {
    return App.instance!!.getString(this)!!
}

fun Int.getResDimen(): Int {
    return App.instance!!.resources.getDimensionPixelSize(this)
}

fun Int.getResColor(): Int {
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

fun Activity.startActivitySafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun Activity.startActivityForResultSafely(intent: Intent, code: Int) {
    try {
        startActivityForResult(intent, code)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}


inline fun <reified T> Activity.createIntent(): Intent {
    return Intent(this, T::class.java)
}

inline fun <reified T> Activity.startActivity() {
    val intent = this.createIntent<T>()
    startActivitySafely(intent)
}

inline fun <reified T> Activity.startActivityForResult(code: Int) {
    val intent = this.createIntent<T>()
    startActivityForResultSafely(intent, code)
}

fun Activity.registerEventBus() {
    if (!EventBus.getDefault().isRegistered(this)) {
        EventBus.getDefault().register(this)
    }
}

fun Activity.unregisterEventBus() {
    if (EventBus.getDefault().isRegistered(this)) {
        EventBus.getDefault().unregister(this)
    }
}