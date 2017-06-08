package com.juniperphoton.myerlist.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.juniperphoton.myerlist.util.StatusBarCompat

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.setUpActivity(this)
    }
}