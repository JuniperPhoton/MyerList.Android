package com.juniperphoton.myerlist.util

import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtil {
    fun show(context: Context, view: View, delay: Int) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.postDelayed({ inputMethodManager.showSoftInput(view, 0) }, delay.toLong())
    }

    fun hide(context: Context, binder: IBinder) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binder, 0)
    }
}