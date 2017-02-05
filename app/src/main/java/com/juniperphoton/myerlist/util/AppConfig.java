package com.juniperphoton.myerlist.util;


import com.juniperphoton.myerlist.App;

public class AppConfig {
    public static boolean hasLogined() {
        String access_token = LocalSettingUtil.getString(App.getInstance(), Params.ACCESS_TOKEN_KEY);
        return access_token != null;
    }

    public static String getSid() {
        return LocalSettingUtil.getString(App.getInstance(), Params.SID_KEY);
    }

    public static String getAccessToken() {
        return LocalSettingUtil.getString(App.getInstance(), Params.ACCESS_TOKEN_KEY);
    }

    public static boolean isInOfflineMode() {
        return LocalSettingUtil.checkKey(App.getInstance(), Params.OFFLINE_MODE);
    }

    public static boolean addToBottom() {
        if (!LocalSettingUtil.checkKey(App.getInstance(), Params.SETTING_ADD_TO_BOTTOM_KEY)) {
            return true;
        }
        return LocalSettingUtil.getBoolean(App.getInstance(), Params.SETTING_ADD_TO_BOTTOM_KEY, true);
    }
}
