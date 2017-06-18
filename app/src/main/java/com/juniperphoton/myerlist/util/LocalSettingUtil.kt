package com.juniperphoton.myerlist.util

import android.content.Context
import android.content.SharedPreferences

@Suppress("Unused")
object LocalSettingUtil {
    private val CONFIG_NAME = "config"

    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
    }

    fun clearAll(context: Context) {
        val sharedPreferences = getSharedPreference(context)
        sharedPreferences.edit().clear().apply()
    }

    fun getBoolean(context: Context, key: String, defaultVal: Boolean): Boolean {
        val sharedPreferences = getSharedPreference(context)
        return sharedPreferences.getBoolean(key, defaultVal)
    }

    fun getInt(context: Context, key: String, defaultVal: Int): Int {
        return getSharedPreference(context).getInt(key, defaultVal)
    }

    fun checkKey(context: Context, key: String): Boolean {
        val sharedPreferences = getSharedPreference(context)
        return sharedPreferences.contains(key)
    }

    fun getString(context: Context, key: String, defValue: String): String? {
        val sharedPreferences = getSharedPreference(context)
        return sharedPreferences.getString(key, defValue)
    }

    fun getString(context: Context, key: String): String? {
        val sharedPreferences = getSharedPreference(context)
        return sharedPreferences.getString(key, null)
    }

    fun putString(context: Context, key: String, value: String): Boolean {
        val sharedPreference = getSharedPreference(context)
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun putBoolean(context: Context, key: String, value: Boolean?): Boolean {
        val sharedPreference = getSharedPreference(context)
        val editor = sharedPreference.edit()
        editor.putBoolean(key, value!!)
        return editor.commit()
    }

    fun putInt(context: Context, key: String, value: Int): Boolean {
        val sharedPreference = getSharedPreference(context)
        val editor = sharedPreference.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun deleteKey(context: Context, key: String): Boolean {
        val sharedPreferences = getSharedPreference(context)
        val editor = sharedPreferences.edit()
        editor.remove(key)
        return editor.commit()
    }
}
