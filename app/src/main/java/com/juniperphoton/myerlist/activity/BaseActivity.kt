package com.juniperphoton.myerlist.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.util.LocalSettingUtil

import com.juniperphoton.myerlist.util.StatusBarCompat
import java.util.*

open class BaseActivity : AppCompatActivity() {
    @Suppress("deprecation")
    override fun onCreate(savedInstanceState: Bundle?) {
        val languageKey = resources.getString(R.string.language_key)
        val language = when (LocalSettingUtil.getInt(this, languageKey, 0)) {
            0 -> Locale.ENGLISH
            else -> Locale.SIMPLIFIED_CHINESE
        }
        val config = resources.configuration
        config.setLocale(language)
        resources.updateConfiguration(config, resources.displayMetrics)

        super.onCreate(savedInstanceState)
        StatusBarCompat.setUpActivity(this)
    }
}