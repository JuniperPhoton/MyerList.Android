package com.juniperphoton.myerlist

import android.app.Application
import android.content.Context

import com.juniperphoton.myerlist.realm.RealmUtils

class App : Application() {
    companion object {
        var instance: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        RealmUtils.init(this)
    }
}