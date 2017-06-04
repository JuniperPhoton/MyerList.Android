package com.juniperphoton.myerlist.realm

import android.content.Context

import io.realm.Realm
import io.realm.RealmConfiguration

object RealmUtils {
    private val FILE_NAME = "MyerList.realm"
    private val SCHEMA_VERSION = 1
    private var sConfiguration: RealmConfiguration? = null

    private var sMainInstance: Realm? = null

    fun init(context: Context) {
        Realm.init(context)
        sConfiguration = RealmConfiguration.Builder()
                .name(FILE_NAME)
                .schemaVersion(SCHEMA_VERSION.toLong())
                .migration(CacheMigration())
                .build()
        sMainInstance = Realm.getInstance(sConfiguration!!)
    }

    val mainInstance: Realm
        get() = sMainInstance!!

    val newInstance: Realm
        get() = Realm.getInstance(sConfiguration!!)
}