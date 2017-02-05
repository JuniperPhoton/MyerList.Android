package com.juniperphoton.myerlist.realm;


import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmUtils {
    private static final String FILE_NAME = "MyerList.realm";
    private static final int SCHEMA_VERSION = 1;
    private static RealmConfiguration sConfiguration;

    private static Realm sMainInstance;

    public static void init(Context context) {
        Realm.init(context);
        sConfiguration = new RealmConfiguration.Builder()
                .name(FILE_NAME)
                .schemaVersion(SCHEMA_VERSION)
                .migration(new CacheMigration())
                .build();

        sMainInstance = Realm.getInstance(sConfiguration);
    }

    public static Realm getMainInstance() {
        return Realm.getInstance(sConfiguration);
    }
}
