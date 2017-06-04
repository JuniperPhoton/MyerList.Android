package com.juniperphoton.myerlist.receiver

import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.juniperphoton.myerlist.api.CloudService
import com.juniperphoton.myerlist.api.response.GetToDosResponse
import com.juniperphoton.myerlist.util.WidgetUpdater
import rx.Subscriber
import rx.schedulers.Schedulers

class WidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        CloudService.getToDos().subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GetToDosResponse>() {
                    override fun onNext(t: GetToDosResponse?) {
                        WidgetUpdater.update(context!!)
                    }

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }
                })
    }
}