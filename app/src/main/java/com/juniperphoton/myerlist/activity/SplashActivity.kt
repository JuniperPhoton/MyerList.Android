package com.juniperphoton.myerlist.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.juniperphoton.myerlist.util.AppConfig

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!AppConfig.logined) {
            val intent = Intent(this, StartActivity::class.java)
            finish()
            startActivity(intent)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            getIntent()?.let {
                intent.action = it.action
                Log.d("SplashActivity", "Copy action:${it.action}")
            }
            finish()
            startActivity(intent)
        }
    }
}