package com.juniperphoton.myerlist.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.event.ReCreateEvent
import com.juniperphoton.myerlist.extension.getResString
import com.juniperphoton.myerlist.util.LocalSettingUtil
import com.juniperphoton.myerlist.widget.SettingsItemLayout
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_settings.*
import org.greenrobot.eventbus.EventBus
import java.util.*

@Suppress("unused", "unused_parameter")
class SettingsActivity : BaseActivity() {
    val changeLanguageView: SettingsItemLayout by lazy {
        change_language_view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)
        updateLocal()

        changeLanguageView.onClick = {
            toggleChangeLanguage()
        }
    }

    @Suppress("deprecation")
    private fun updateLocal() {
        val resources = resources
        val config = resources.configuration
        val locale = config.locale
        if (locale == Locale.SIMPLIFIED_CHINESE) {
            change_language_view.content = R.string.change_lang_hint_chinese.getResString()
        } else {
            change_language_view.content = R.string.change_lang_hint_english.getResString()
        }
    }

    @Suppress("deprecation")
    private fun getCurrentLocale(config: Configuration): Locale {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return config.locales[0]
        } else {
            return config.locale
        }
    }

    @Suppress("deprecation")
    private fun toggleChangeLanguage() {
        val locale = getCurrentLocale(resources.configuration)
        val opts = resources.getStringArray(R.array.language)
        val defaultIndex = when (locale) {
            Locale.SIMPLIFIED_CHINESE -> 1
            else -> 0
        }
        AlertDialog.Builder(this)
                .setTitle(R.string.change_lang)
                .setSingleChoiceItems(opts, defaultIndex) { dialog, which ->
                    dialog.dismiss()
                    if (defaultIndex != which) {
                        val languageKey = resources.getString(R.string.language_key)
                        LocalSettingUtil.putInt(this, languageKey, which)
                        EventBus.getDefault().postSticky(ReCreateEvent())
                        finish()
                    }
                }
                .create().show()
    }

    @OnClick(R.id.settings_logout)
    internal fun onClickLogout() {
        AlertDialog.Builder(this)
                .setTitle(R.string.confirm_to_logout.getResString())
                .setPositiveButton(R.string.yes.getResString()) { _, _ ->
                    LocalSettingUtil.clearAll(App.instance!!)
                    Realm.getDefaultInstance().executeTransaction {
                        it.deleteAll()
                    }
                    val intent = Intent(App.instance!!, StartActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .setNegativeButton(R.string.no.getResString()) { dialog, _ -> dialog.dismiss() }
                .create().show()
    }
}
