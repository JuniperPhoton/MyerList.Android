package com.juniperphoton.myerlist.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class CacheMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        if (oldVersion == 1L) {
        }
    }
}