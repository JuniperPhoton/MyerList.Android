package com.juniperphoton.myerlistandroid.realm;


import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class CacheMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 1) {
            schema.get("ToDo")
                    .addField("deleted", boolean.class);
            oldVersion++;
        }
    }
}
