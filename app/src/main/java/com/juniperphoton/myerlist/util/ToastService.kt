package com.juniperphoton.myerlist.util

import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast

import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R

object ToastService {
    private val handler = Handler(Looper.getMainLooper())

    fun sendShortToast(str: String) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            handler.post { sendInternal(str) }
        } else {
            sendInternal(str)
        }
    }

    fun sendShortToast(resId: Int) {
        sendShortToast(App.instance!!.getString(resId))
    }

    private fun sendInternal(str: String) {
        val inflater = LayoutInflater.from(App.instance)
        val view = inflater.inflate(R.layout.toast_layout, null)

        val textView = view.findViewById(R.id.toast_textView) as TextView
        textView.text = str

        val toast = Toast(App.instance)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.setGravity(Gravity.BOTTOM, 0, 100)
        toast.show()
    }
}
