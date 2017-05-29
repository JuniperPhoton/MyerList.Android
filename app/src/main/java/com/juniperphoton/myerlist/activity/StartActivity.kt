package com.juniperphoton.myerlist.activity

import android.content.Intent
import android.os.Bundle
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.util.Params

@Suppress("unused","unused_parameter")
class StartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.loginButton)
    fun onClickLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(Params.LOGIN_MODE, Params.LoginMode.LOGIN)
        startActivity(intent)
    }

    @OnClick(R.id.register_btn)
    fun onClickRegister() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(Params.LOGIN_MODE, Params.LoginMode.REGISTER)
        startActivity(intent)
    }

    @OnClick(R.id.offline_btn)
    fun onClickOffline() {
    }
}
