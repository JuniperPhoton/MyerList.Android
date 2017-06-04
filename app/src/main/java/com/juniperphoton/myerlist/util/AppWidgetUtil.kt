package com.juniperphoton.myerlist.util

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.receiver.WidgetProvider

object AppWidgetUtil {
    fun doWithWidgetId(block: ((Int) -> Unit)) {
        val ids = AppWidgetManager.getInstance(App.instance)
                .getAppWidgetIds(ComponentName(App.instance, WidgetProvider::class.java))
        ids.forEach {
            block.invoke(it)
        }
    }
}