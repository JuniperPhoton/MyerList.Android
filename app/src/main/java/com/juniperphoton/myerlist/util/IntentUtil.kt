package com.juniperphoton.myerlist.util

import android.app.PendingIntent
import android.content.Intent
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.activity.SplashActivity

object IntentUtil {
    fun getPendingIntentForMain(): PendingIntent {
        val pendingIntent = PendingIntent.getActivity(App.instance, 0, Intent(App.instance, SplashActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent
    }

    fun getAddingPendingIntentForMain(): PendingIntent {
        val intent = Intent(App.instance, SplashActivity::class.java)
        intent.action = "action.add"
        val pendingIntent = PendingIntent.getActivity(App.instance, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent
    }
}