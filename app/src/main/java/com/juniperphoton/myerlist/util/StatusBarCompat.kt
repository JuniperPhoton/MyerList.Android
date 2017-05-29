package com.juniperphoton.myerlist.util

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View

object StatusBarCompat {
    fun setUpActivity(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 19 && !isChrome) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= 21) {
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    val isChrome: Boolean
        get() = Build.BRAND === "chromium" || Build.BRAND === "chrome"
}