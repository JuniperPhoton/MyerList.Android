package com.juniperphoton.myerlist;


import android.app.Application;
import android.content.Context;

import com.juniperphoton.myerlist.realm.RealmUtils;

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
