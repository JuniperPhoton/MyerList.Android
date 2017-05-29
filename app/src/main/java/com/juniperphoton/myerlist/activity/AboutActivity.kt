package com.juniperphoton.myerlist.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.juniperphoton.myerlist.R

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.util.getVersionName
import moe.feng.alipay.zerosdk.AlipayZeroSdk

@Suppress("unused")
class AboutActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.version_tv)
    var versionTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ButterKnife.bind(this)

        updateVersion()
    }

    private fun updateVersion() {
        versionTextView!!.text = "Version ${this.getVersionName()}"
    }

    @OnClick(R.id.email_rl)
    internal fun emailClick(view: View) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("dengweichao@hotmail.com")) // recipients

        val SHARE_SUBJECT = "MyerList for Android %s feedback"
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(SHARE_SUBJECT, this.getVersionName()))
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")

        startActivitySafely(emailIntent)
    }

    @OnClick(R.id.activity_about_rate_rl)
    internal fun rateClick(view: View) {
        val uri = Uri.parse("market://details?id=" + packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivitySafely(intent)
    }

    @OnClick(R.id.activity_about_donate_rl)
    internal fun donateClick(view: View) {
        if (AlipayZeroSdk.hasInstalledAlipayClient(this)) {
            AlipayZeroSdk.startAlipayClient(this, "aex09127b4dbo4o7fbvcyb0")
        }
    }

    fun startActivitySafely(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}
