package com.juniperphoton.myerlist.util

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.model.ToDo
import com.juniperphoton.myerlist.realm.RealmUtils
import com.juniperphoton.myerlist.receiver.ListViewUpdateService

object WidgetUpdater {
    fun update(context: Context) {
        AppWidgetUtil.doWithWidgetId {
            updateWidget(context, it)
        }
    }

    private fun updateWidget(context: Context, widgetId: Int) {
        val manager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)

        val count = RealmUtils.mainInstance.where(ToDo::class.java)
                .notEqualTo(ToDo.KEY_DELETED, true)
                .equalTo(ToDo.KEY_IS_DONE, ToDo.VALUE_UNDONE).count()

        val intent = Intent(context, ListViewUpdateService::class.java)
        remoteViews.setRemoteAdapter(R.id.widget_list_view, intent)
        remoteViews.setTextViewText(R.id.widget_undone_count, count.toString())
        remoteViews.setTextViewText(R.id.widget_undone_title, R.string.undone_title.getResString())
        remoteViews.setOnClickPendingIntent(R.id.widget_root, IntentUtil.getPendingIntentForMain())
        remoteViews.setOnClickPendingIntent(R.id.widget_add, IntentUtil.getAddingPendingIntentForMain())
        remoteViews.setPendingIntentTemplate(R.id.widget_list_view, IntentUtil.getPendingIntentForMain())

        manager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_list_view)
        manager.updateAppWidget(widgetId, remoteViews)
    }
}