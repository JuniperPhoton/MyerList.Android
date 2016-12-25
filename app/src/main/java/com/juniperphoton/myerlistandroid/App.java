package com.juniperphoton.myerlistandroid;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.juniperphoton.myerlistandroid.realm.RealmUtils;

import io.realm.Realm;

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        RealmUtils.init(this);
    }

    public static Context getInstance() {
        return mContext;
    }
}
