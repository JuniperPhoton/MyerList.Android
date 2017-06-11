package com.juniperphoton.myerlist.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.getResString
import com.juniperphoton.myerlist.extension.getVersionName
import com.juniperphoton.myerlist.extension.startActivitySafely
import kotlinx.android.synthetic.main.activity_about.*
import moe.feng.alipay.zerosdk.AlipayZeroSdk

@Suppress("unused", "unused_parameter")
class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ButterKnife.bind(this)

        updateVersion()
    }

    private fun updateVersion() {
        versionTextView.text = "${this.getVersionName()}"
    }

    @OnClick(R.id.emailView)
    internal fun onClickEmail(view: View) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_dev)))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MyerList for Android ${this.getVersionName()} feedback")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")

        startActivitySafely(emailIntent)
    }

    @OnClick(R.id.rateView)
    internal fun onClickRate() {
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivitySafely(intent)
    }

    @OnClick(R.id.donateView)
    internal fun onClickDonate() {
        if (AlipayZeroSdk.hasInstalledAlipayClient(this)) {
            AlipayZeroSdk.startAlipayClient(this, "aex09127b4dbo4o7fbvcyb0")
        }
    }

    @OnClick(R.id.githubView)
    internal fun onClickGithub() {
        val uri = Uri.parse(R.string.github_url.getResString())
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivitySafely(intent)
    }
}
