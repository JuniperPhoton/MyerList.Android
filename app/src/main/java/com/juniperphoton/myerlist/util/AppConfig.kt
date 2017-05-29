package com.juniperphoton.myerlist.util


import com.juniperphoton.myerlist.App

object AppConfig {
    val logined: Boolean = LocalSettingUtil.getString(App.instance!!, Params.ACCESS_TOKEN_KEY) != null

    val sid: String? = LocalSettingUtil.getString(App.instance!!, Params.SID_KEY)

    val accessToken: String? = LocalSettingUtil.getString(App.instance!!, Params.ACCESS_TOKEN_KEY)

    val isInOfflineMode: Boolean = LocalSettingUtil.checkKey(App.instance!!, Params.OFFLINE_MODE)

    fun addToBottom(): Boolean {
        if (!LocalSettingUtil.checkKey(App.instance!!, Params.SETTING_ADD_TO_BOTTOM_KEY)) {
            return true
        }
        return LocalSettingUtil.getBoolean(App.instance!!, Params.SETTING_ADD_TO_BOTTOM_KEY, true)
    }
}
