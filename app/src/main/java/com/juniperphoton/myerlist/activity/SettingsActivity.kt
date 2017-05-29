package com.juniperphoton.myerlist.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.App
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.event.ReCreateEvent
import com.juniperphoton.myerlist.util.LocalSettingUtil
import com.juniperphoton.myerlist.util.getResString
import kotlinx.android.synthetic.main.activity_settings.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)
        changeLangView!!.onClick = {
            onClickChangeLanguage()
        }
        updateLocal()
    }

    private fun updateLocal() {
        val resources = resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        val locale = config.locale
        if (locale == Locale.SIMPLIFIED_CHINESE) {
            changeLangView!!.content = R.string.change_lang_hint_chinese.getResString()!!
        } else {
            changeLangView!!.content = R.string.change_lang_hint_english.getResString()!!
        }
    }

    internal fun onClickChangeLanguage() {
        val resources = resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        val locale = config.locale
        val opts = resources.getStringArray(R.array.language)
        var defaultIndex = 0
        if (locale == Locale.SIMPLIFIED_CHINESE) {
            defaultIndex = 1
        }
        Log.d("settings", locale.toString())
        val builder = AlertDialog.Builder(this)
        val finalDefaultIndex = defaultIndex
        builder.setTitle(R.string.change_lang)
                .setSingleChoiceItems(opts, defaultIndex) { dialog, which ->
                    dialog.dismiss()
                    if (finalDefaultIndex != which) {
                        config.setLocale(if (which == 0) Locale.ENGLISH else Locale.SIMPLIFIED_CHINESE)
                        resources.updateConfiguration(config, dm)
                        EventBus.getDefault().postSticky(ReCreateEvent())
                        finish()
                    }
                }
                .create().show()
    }

    @OnClick(R.id.settings_logout)
    internal fun onClickLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.confirm_to_logout.getResString()!!)
                .setPositiveButton(R.string.confirm_ok.getResString()!!) { _, _ ->
                    LocalSettingUtil.clearAll(App.instance!!)
                    val intent = Intent(App.instance!!, StartActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .setNegativeButton(R.string.confirm_cancel.getResString()!!) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}
