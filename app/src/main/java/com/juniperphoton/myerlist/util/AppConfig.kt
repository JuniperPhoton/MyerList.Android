package com.juniperphoton.myerlist.util


import com.juniperphoton.myerlist.App

object AppConfig {
    val logined: Boolean
        get() = LocalSettingUtil.getString(App.instance!!, Params.ACCESS_TOKEN_KEY) != null

    val sid: String?
        get() = LocalSettingUtil.getString(App.instance!!, Params.SID_KEY)

    val accessToken: String?
        get() = LocalSettingUtil.getString(App.instance!!, Params.ACCESS_TOKEN_KEY)

    val isInOfflineMode: Boolean
        get() = LocalSettingUtil.checkKey(App.instance!!, Params.OFFLINE_MODE)

    fun addToBottom(): Boolean {
        if (!LocalSettingUtil.checkKey(App.instance!!, Params.KEY_ADD_TO_BOTTOM)) {
            return true
        }
        return LocalSettingUtil.getBoolean(App.instance!!, Params.KEY_ADD_TO_BOTTOM, true)
    }
}
