package com.juniperphoton.myerlist.realm

import android.content.Context

import io.realm.Realm
import io.realm.RealmConfiguration

object RealmUtils {
    private val FILE_NAME = "MyerList.realm"
    private val SCHEMA_VERSION = 1

    fun init(context: Context) {
        Realm.init(context)
        var config = RealmConfiguration.Builder()
                .name(FILE_NAME)
                .schemaVersion(SCHEMA_VERSION.toLong())
                .migration(CacheMigration())
                .build()
        Realm.setDefaultConfiguration(config)
    }
}