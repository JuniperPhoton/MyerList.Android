package com.juniperphoton.myerlistandroid;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import io.realm.Realm;

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Realm.init(this);
    }

    public static Context getInstance() {
        return mContext;
    }
}
