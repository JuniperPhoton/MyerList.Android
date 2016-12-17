package com.juniperphoton.myerlistandroid.util;


import com.juniperphoton.myerlistandroid.App;

public class AppConfig {
    public static boolean hasLogined() {
        String access_token = LocalSettingUtil.getString(App.getInstance(), Params.ACCESS_TOKEN_KEY);
        return access_token != null;
    }
}
